package com.itsaur.internship.query.cart;

import com.beust.ah.A;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class CartQuery implements CartQueryModelStore {

    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public CartQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }

//    @Override
//    public Future<List<CartQueryModel.CartItemQueryModel>> findByUserId(UUID uid) {
//        return pgPool
//                .preparedQuery(
//                        "SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity, p.name, p.price " +
//                                "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid JOIN product p on p.pid = ci.pid " +
//                                "WHERE uid = $1 " +
//                                "ORDER BY ci.itemid;")
//                .execute(Tuple.of(uid))
//                .compose(records -> {
//                    ArrayList<CartQueryModel.CartItemQueryModel> items = new ArrayList<>();
//                    try {
//                        records.forEach(row -> {
//                            CartQueryModel.CartItemQueryModel item = new CartQueryModel.CartItemQueryModel(
//                                    row.getUUID("pid"),
//                                    row.getString("name"),
//                                    row.getInteger("price") * row.getInteger("quantity"),
//                                    row.getInteger("quantity"));
//                            items.add(item);
//                        });
//                    } catch (NoSuchElementException e) {
//                        return Future.succeededFuture(new ArrayList<>());
////                        return Future.failedFuture(new IllegalArgumentException("Cart not found"));
//                    }
////                    if (records.iterator().next().getUUID("itemid") != null) {
//
////                    }
////                    else return Future.succeededFuture(new ArrayList<>());
////                    CartQueryModel cart = new CartQueryModel(items);
//                    return Future.succeededFuture(items);
//                });
//    }

    @Override
    public Future<CartQueryModel> findByUserId(UUID uid) {
        return pgPool
                .preparedQuery("SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity, p.name, p.price " +
                        "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid JOIN product p on p.pid = ci.pid " +
                        "WHERE uid = $1 " +
                        "ORDER BY ci.itemid;")
                .execute(Tuple.of(uid))
                .compose(cartItemsRecords -> pgPool
                        .preparedQuery("SELECT sum(p.price * ci.quantity) " +
                                "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid JOIN product p on p.pid = ci.pid " +
                                "WHERE c.uid=$1")
                        .execute(Tuple.of(uid))
                        .compose(record -> {
                            List<CartQueryModel.CartItemQueryModel> cartItemsList = new ArrayList<>();
                            CartQueryModel cartQueryModel;
                            try {
                                double totalPrice = record.iterator().next().getDouble("sum");
                                cartItemsRecords.forEach(row -> {
                                    UUID pid = row.getUUID("pid");
                                    String name = row.getString("name");
                                    double price = row.getDouble("price");
                                    int quantity = row.getInteger("quantity");

                                    CartQueryModel.CartItemQueryModel cartItemQueryModel = new CartQueryModel
                                            .CartItemQueryModel(pid, name, price * quantity, quantity);
                                    cartItemsList.add(cartItemQueryModel);
                                });
                                cartQueryModel = new CartQueryModel(cartItemsList, totalPrice);
                            } catch (Exception e) {
                                return Future.succeededFuture(new CartQueryModel(new ArrayList<>(), 0.0));
                            }
                            return Future.succeededFuture(cartQueryModel);
                        }));
    }

    @Override
    public Future<Double> totalPrice(UUID uid) {
        return pgPool
                .preparedQuery("SELECT sum(p.price * ci.quantity) " +
                        "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid JOIN product p on p.pid = ci.pid " +
                        "WHERE c.uid=$1")
                .execute(Tuple.of(uid))
                .compose(record -> {
                    try {
                        double totalPrice = record.iterator().next().getDouble("sum");
                        return Future.succeededFuture(totalPrice);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("there are no products in cart"));
                    }
                });
    }
}
