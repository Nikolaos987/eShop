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
import java.util.Collection;
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
    public Future<Void> insert(CartItem item, UUID cid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
            return inStock(item, 0)
                    .compose(isInStock -> client
                            .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4);")
                            .execute(Tuple.of(item.itemId(), cid, item.pid(), item.quantity()))
                            .compose(records -> client.close())
                            .compose(result -> Future.succeededFuture()));
    }

    @Override
    public Future<Void> insert(Cart cart) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("INSERT INTO cart VALUES ($1, $2, $3);")
                .execute(Tuple.of(cart.cid(), cart.uid(), cart.dateCreated()))
                .compose(records -> client.close())
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<CartItem> findCartItem(UUID itemid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT c.cid, c.uid, datecreated, itemid, ci.pid, ci.quantity FROM cart c JOIN cartitem ci ON c.cid = ci.cid WHERE itemid = $1;")
                .execute(Tuple.of(itemid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    CartItem item = new CartItem(row.getUUID("itemid"), row.getUUID("pid"), row.getInteger("quantity"));
                    return Future.succeededFuture(item);
                });
    }

    @Override
    public Future<CartItem> findCartItem(UUID uid, UUID pid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT c.cid, c.uid, datecreated, itemid, ci.pid, ci.quantity FROM cart c JOIN cartitem ci ON c.cid = ci.cid WHERE uid = $1 AND pid = $2;")
                .execute(Tuple.of(uid, pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    CartItem item = new CartItem(row.getUUID("itemid"), row.getUUID("pid"), row.getInteger("quantity"));
                    return Future.succeededFuture(item);
                });
    }

    @Override
    public Future<ArrayList<CartItem>> findCartItems(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT c.cid, c.uid, datecreated, itemid, ci.pid, ci.quantity FROM cart c JOIN cartitem ci ON c.cid = ci.cid WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    ArrayList<CartItem> items = new ArrayList<>();
                    records.forEach(row -> {
                        CartItem item = new CartItem(row.getUUID("itemid"), row.getUUID("pid"), row.getInteger("quantity"));
                        items.add(item);
                    });
                    return Future.succeededFuture(items);
                });
    }

    @Override
    public Future<Cart> findCart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .otherwiseEmpty()
                .compose(records -> {
                    Collection<CartItem> items = new ArrayList<>();
                    if (records.size() > 1) {
                        records.forEach(row -> {
                            CartItem item = new CartItem(row.getUUID("itemid"), row.getUUID("pid"), row.getInteger("quantity"));
                            items.add(item);
                        });
                    } else {
                        items.add(null);
                    }
                    Row row = records.iterator().next();
                    Cart cart = new Cart(row.getUUID("cid"), row.getUUID("uid"), row.getLocalDateTime("datecreated"), items);
                    return Future.succeededFuture(cart);
                });
    }

    @Override
    public Future<Void> removeCartItems(ArrayList<CartItem> items) {
        return removeNext(items, 0)
                .compose(result -> Future.succeededFuture());
    }

    public Future<Void> removeNext(ArrayList<CartItem> items, int position) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("DELETE FROM cartitem WHERE itemid = $1;")
                .execute(Tuple.of(items.get(position).itemId()))
                .compose(records -> {
                    if (position + 1 < items.size()) {
                        return removeNext(items, position + 1);
                    }
                    return Future.succeededFuture();
                });
    }

    @Override
    public Future<Void> updateCartItem(CartItem item, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return inStock(item, quantity)
                .compose(isInStock -> {
                    if (item.quantity() + quantity >= 0)
                        return client
                                .preparedQuery("UPDATE cartitem SET quantity = quantity + $1 WHERE itemid = $2;")
                                .execute(Tuple.of(quantity, item.itemId()))
                                .compose(records -> Future.succeededFuture());
                    return Future.failedFuture("item quantity can't be negative!");
                });
    }

    public Future<Void> inStock(CartItem item, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                .execute(Tuple.of(item.pid()))
                .compose(records -> {
                    Row row = records.iterator().next();
                    if (row.getInteger("quantity") - (item.quantity() + quantity) >= 0)
                        return Future.succeededFuture();
                    return Future.failedFuture(new IllegalArgumentException("product out of stock!"));
                });
    }

}
