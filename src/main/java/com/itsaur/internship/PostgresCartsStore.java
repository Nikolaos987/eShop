package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDateTime;
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
    public Future<Void> checkQuantity(UUID id, int quantity) {
        if (quantity > 0) {
            SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
            return client
                    .preparedQuery("SELECT * FROM product WHERE pid = $1")
                    .execute(Tuple.of(id))
                    .compose(rows -> {
                        Row row = rows.iterator().next();
                        client.close().onSuccess(v -> Future.succeededFuture()); // TODO: 27/6/23
                        if (row.getInteger("quantity") - quantity >= 0) {
                            return Future.succeededFuture();
                        }
                        return Future.failedFuture(new IllegalArgumentException("out of stock"));
                    });
        }
        return Future.failedFuture(new IllegalArgumentException("quantity can not be 0 or less"));
    }

    @Override
    public Future<Void> addToCart(UUID uid, UUID pid, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return findCart(uid)
                .compose(foundCart -> {
                    if (foundCart) {
                        return findItem(uid, pid).compose(foundItem -> {
                            if (!foundItem) {
                                        client.preparedQuery("INSERT INTO cartitem (cid, pid, quantity) SELECT cid, $1, $2 FROM cart WHERE uid = $3;")
                                        .execute(Tuple.of(pid, quantity, uid))
                                        .compose(v -> Future.succeededFuture());
                            }
                            return client.preparedQuery("SELECT cartitem.cid, quantity FROM cart JOIN cartitem ON cart.cid = cartitem.cid WHERE uid = $1")
                                    .execute(Tuple.of(uid))
                                    .compose(records -> checkQuantity(pid, quantity + records.iterator().next().getInteger("quantity"))
                                            .compose(amount -> client.preparedQuery("UPDATE cartitem SET quantity = quantity + $1 WHERE cid = $2 AND pid = $3;")
                                                    .execute(Tuple.of(quantity, records.iterator().next().getUUID("cid"), pid))
                                                    .compose(v -> Future.succeededFuture()))
                                            .compose(v -> Future.succeededFuture())); // TODO: 27/6/23 remove this line)
                        });
                    }
                    return client.preparedQuery("INSERT INTO cart ("uid", datecreated) VALUES ($1, $2);")
                            .execute(Tuple.of(uid, LocalDateTime.now()))
                            .compose(v -> Future.succeededFuture())
                            .compose(res -> client.preparedQuery("INSERT INTO cartitem (cid, pid, quantity) SELECT cid, $1, $2 FROM cart WHERE uid = $3;")
                                    .execute(Tuple.of(pid, quantity, uid))
                                    .compose(v -> Future.succeededFuture()));
                });
    }

    @Override
    public Future<Boolean> findCart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM cart JOIN cartitem ON cart.cid = cartitem.cid WHERE cart.uid = $1")
                .execute(Tuple.of(uid))
                .compose(rows -> {
                    client.close().onSuccess(v -> Future.succeededFuture());
                    if (rows.size() != 0)
                        return Future.succeededFuture(true);
                    else
                        return Future.succeededFuture(false);
                });
    }

    @Override
    public Future<Boolean> findItem(UUID uid, UUID pid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM cartitem WHERE pid = $2 AND cid IN (SELECT cid FROM cart WHERE uid = $1)")
                .execute(Tuple.of(uid, pid))
                .compose(rows -> {
                    client.close().onSuccess(v -> Future.succeededFuture());
                    if (rows.size() != 0)
                        return Future.succeededFuture(true);
                    else
                        return Future.succeededFuture(false);
                });
    }

    @Override
    public Future<Void> removeFromCart(UUID uid, UUID pid, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client.preparedQuery("SELECT cartitem.cid, quantity FROM cart JOIN cartitem ON cart.cid = cartitem.cid WHERE uid = $1")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    int itemQuantity = records.iterator().next().getInteger("quantity");
                    UUID cid = records.iterator().next().getUUID("cid");
                    if (itemQuantity - quantity > 0) {
                        return client
                                .preparedQuery("UPDATE cartitem SET quantity = quantity - $1 WHERE cid = $2 AND pid = $3")
                                .execute(Tuple.of(quantity, cid, pid))
                                .compose(v -> client.close())
                                .compose(v -> Future.succeededFuture());
                    } else {
                        return client
                                .preparedQuery("DELETE FROM cartitem WHERE cid = $1 AND pid = $2")
                                .execute(Tuple.of(cid, pid))
                                .compose(v -> client.close())
                                .compose(v -> Future.succeededFuture());
                    }
                });
    }

    @Override
    public Future<JsonArray> cart(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT cart.cid, cart.uid, datecreated, itemid, product.pid, cartitem.quantity, price, name FROM cart JOIN cartitem ON cart.cid = cartitem.cid JOIN product ON product.pid = cartitem.pid WHERE uid = $1")
                .execute(Tuple.of(uid))
                .compose(rows -> {
                    JsonArray cartProducts = new JsonArray();
                    rows.forEach(row -> cartProducts.add(JsonObject.of(
                            "PRODUCT ID", row.getUUID("pid"),
                            "NAME", row.getString("name"),
                            "PRICE", row.getDouble("price"),
                            "QUANTITY", row.getInteger("quantity"))));
                    if (cartProducts.size() != 0) {
                        client.close().onSuccess(v -> Future.succeededFuture());
                        return Future.succeededFuture(cartProducts);
                    }
                    return Future.failedFuture(new IllegalArgumentException("Seems like your cart is empty"));
                });
    }

    @Override
    public Future<Void> buy(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT cart.cid, quantity FROM cart JOIN cartitem ON cart.cid = cartitem.cid WHERE uid = $1")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    int quantity = records.iterator().next().getInteger("quantity");
                    UUID cid = records.iterator().next().getUUID("cid");
                    return removeQuantity(uid).compose(r -> client
                            .preparedQuery("DELETE FROM cartitem WHERE cid = $1")
                            .execute(Tuple.of(cid))
                            .compose(r2 -> client
                                    .preparedQuery("DELETE FROM cart WHERE cid = $1")
                                    .execute(Tuple.of(cid))
                                    .compose(res -> client.close())
                                    .compose(res -> Future.succeededFuture())));
                });
    }

    public Future<Void> removeQuantity(UUID userid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT pid, quantity FROM cart JOIN cartitem ON cart.cid = cartitem.cid WHERE uid = $1")
                .execute(Tuple.of(userid))
                .compose(rows -> {
                    ArrayList<UUID> ids = new ArrayList<>();
                    ArrayList<Integer> quantities = new ArrayList<>();
                    rows.forEach(row -> {
                        UUID userId = row.getUUID("pid");
                        ids.add(userId);
                        int quantity = row.getInteger("quantity");
                        quantities.add(quantity);
                    });
                    return removeNext(ids, quantities, 0, client);
                });
    }

    public Future<Void> removeNext(ArrayList<UUID> ids, ArrayList<Integer> quantities, int pos, SqlClient client) {
        if (pos < ids.size()) {
            return client
                    .preparedQuery("UPDATE product SET quantity = quantity - $1 WHERE pid = $2;")
                    .execute(Tuple.of(quantities.get(pos), ids.get(pos)))
                    .compose(rows -> removeNext(ids, quantities, pos+1, client));
        } return client.close()
                .compose(r -> Future.succeededFuture());
    }

}
