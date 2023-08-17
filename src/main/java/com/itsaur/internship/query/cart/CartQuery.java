package com.itsaur.internship.query.cart;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.UUID;

public class CartQuery implements CartQueryModelStore {

    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public CartQuery(int port, String host, String db, String user, String password, int poolSize) {
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
    public Future<ArrayList<CartQueryModel.CartItemQueryModel>> findByUserId(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery(
                        "SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity, p.name, p.price " +
                        "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid JOIN product p on p.pid = ci.pid " +
                        "WHERE uid = $1 " +
                        "ORDER BY ci.itemid;")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    ArrayList<CartQueryModel.CartItemQueryModel> items = new ArrayList<>();
                    if (records.iterator().next().getUUID("itemid") != null) {
                        records.forEach(row -> {
                            CartQueryModel.CartItemQueryModel item = new CartQueryModel.CartItemQueryModel(
                                    row.getUUID("pid"),
                                    row.getString("name"),
                                    row.getInteger("price") * row.getInteger("quantity"),
                                    row.getInteger("quantity"));
                            items.add(item);
                        });
                    }
//                    CartQueryModel cart = new CartQueryModel(items);
                    return client.close()
                            .compose(r -> Future.succeededFuture(items));
                });
    }
}
