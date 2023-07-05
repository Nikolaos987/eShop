package cartEntity;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.UUID;

public class PostgresCartsStore implements CartsStore {

    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public PostgresCartsStore(int port, String host, String db, String user, String password, int poolSize) {
        this.connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(db)
                .setUser(user)
                .setPassword(password);
        this.poolOptions = new PoolOptions()
                .setMaxSize(poolSize);
    }


    @Override
    public Future<Void> insert(Cart cart) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        cart.items().iterator().forEachRemaining(cartItem -> {
                client
                        .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                        .execute(Tuple.of(cart.items().iterator().next().pid()))
                        .compose(records -> {
                            if (cartItem.quantity() > 0) {
                                if (records.iterator().next().getInteger("quantity") - cartItem.quantity() >= 0)
                                    return Future.succeededFuture();
                                return Future.failedFuture(new IllegalArgumentException("out of stock"));
                            }
                            return Future.failedFuture(new IllegalArgumentException("quantity can not be 0 nor negative"));
                        })
                        .onSuccess(v -> client
                                .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4);")
                                .execute(Tuple.of(cartItem.itemId(), cart.cid(), cartItem.pid(), cartItem.quantity()))
                                .compose(v1 -> Future.succeededFuture()));
        });
        return Future.succeededFuture();
    }

    @Override
    public Future<Cart> findCart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    ArrayList<CartItem> items = new ArrayList<>();
                    if (records.iterator().next().getUUID("itemid") != null) {
                        records.forEach(row -> {
                            CartItem item = new CartItem(row.getUUID("itemid"), row.getUUID("pid"), row.getInteger("quantity"));
                            items.add(item);
                        });
                    }
                    Row row = records.iterator().next();
                    Cart cart = new Cart(row.getUUID("cid"), row.getUUID("uid"), row.getLocalDateTime("datecreated"), items);
                    return Future.succeededFuture(cart);
                });
    }

    @Override
    public Future<Void> deleteCart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return findCart(uid)
                .compose(cart -> {
                    if (cart.items().iterator().next() != null) {
                        return client
                                .preparedQuery("SELECT pid, quantity FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE uid = $1;")
                                .execute(Tuple.of(uid))
                                .onSuccess(records -> {
                                    records.forEach(row -> {
                                        if (row.getInteger("quantity") - cart.items().iterator().next().quantity() >= 0) {
                                            client
                                                    .preparedQuery("UPDATE product SET quantity = quantity - (SELECT quantity FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE pid = $1 AND uid = $2) WHERE pid = $1")
                                                    .execute(Tuple.of(row.getUUID("pid"), uid));
                                        }
                                    });
                                })
                                .compose(res -> client
                                        .preparedQuery("DELETE FROM cartitem WHERE itemid IN (SELECT itemid FROM cart c JOIN cartitem ci ON c.cid = ci.cid WHERE c.uid = $1);")
                                        .execute(Tuple.of(uid))
                                        .compose(v -> Future.succeededFuture()));
                    }
                    return Future.failedFuture(new IllegalArgumentException("you don't have any items in your cart"));
                });
    }

    @Override
    public Future<Void> update(Cart cart) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        cart.items().iterator().forEachRemaining(cartItem -> {
            if (cartItem.quantity() >= 0)
                client
                        .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                        .execute(Tuple.of(cartItem.pid()))
                        .compose(records -> {
                            Row row = records.iterator().next();
                            if (row.getInteger("quantity") - cartItem.quantity() >= 0 && cartItem.quantity() != 0)
                                return Future.succeededFuture();
                            return client
                                    .preparedQuery("DELETE FROM cartitem WHERE itemid = $1")
                                    .execute(Tuple.of(cart.items().iterator().next().itemId()))
                                    .compose(v -> Future.failedFuture(new IllegalArgumentException("no")));
                        })
                        .compose(v -> client
                                .preparedQuery("UPDATE cartitem SET quantity = $1 WHERE cid = $2 AND pid = $3")
                                .execute(Tuple.of(cartItem.quantity(), cart.cid(), cartItem.pid()))
                                .compose(v2 -> Future.succeededFuture()));
        });
        return Future.succeededFuture();
    }

}
