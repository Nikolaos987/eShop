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
                    return client.close().compose(r -> Future.succeededFuture(cart));
                });
    }

    @Override
    public Future<Void> deleteCart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return findCart(uid)
                .compose(cart -> {
                    if (cart != null) {
                        return client
                                .preparedQuery("DELETE FROM cartitem WHERE uid = $1;")
                                .execute(Tuple.of(uid))
                                .compose(r -> client.close()
                                        .compose(v -> client
                                        .preparedQuery("DELETE FROM cart WHERE uid = $1")
                                        .execute(Tuple.of(uid))
                                        .compose(r2 -> client.close())
                                        .compose(v2 -> Future.succeededFuture())));
                    }
                    else
                        return Future.failedFuture(new IllegalArgumentException("you don't have any items in your cart"));
                });
    }

    @Override
    public Future<Void> update(Cart cart) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT cid FROM cart WHERE uid = $1")
                .execute(Tuple.of(cart.uid()))
                .compose(r -> client.close()
                        .compose(records -> deleteNext(cart, 0)
                                .compose(r2 -> updateNext(cart, 0)
                                        .compose(r3 -> Future.succeededFuture()))));

    }

    public Future<Void> deleteNext(Cart cart, int position) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        CartItem cartItem = cart.items().get(position);
        return client
                .preparedQuery("DELETE FROM cartitem WHERE pid = $1 AND cid = $2")
                .execute(Tuple.of(cartItem.pid(), cart.cid()))
                .compose(r -> client.close()
                        .compose(r2 -> {
                            if (position+1 < cart.items().size())
                                return deleteNext(cart, position+1);
                            else
                                return Future.succeededFuture();
                        }));
    }

    public Future<Void> updateNext(Cart cart, int position) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        CartItem cartItem = cart.items().get(position);
        return client
                .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4);")
                .execute(Tuple.of(cartItem.itemId(), cart.cid(), cartItem.pid(), cartItem.quantity()))
                .compose(r -> client.close()
                        .compose(r2 -> {
                            if (position+1 < cart.items().size())
                                return updateNext(cart, position+1);
                            else
                                return Future.succeededFuture();
                        }));
    }

}
