package com.itsaur.internship.cartEntity;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.ArrayList;
import java.util.UUID;

public class PostgresCartsStore implements CartsStore {

    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public PostgresCartsStore(PgPool pgPool) {
        this.pgPool = pgPool;
    }


    @Override
    public Future<Void> insert(Cart cart) {
        return pgPool
                .preparedQuery("INSERT INTO cart VALUES ($1, $2, $3);")
                .execute(Tuple.of(cart.cid(), cart.uid(), cart.dateCreated()))
                .compose(result -> Future.succeededFuture());

//        return client
//                .preparedQuery("SELECT cid FROM cart WHERE cid = $1")
//                .execute(Tuple.of(cart.cid()))
//                .otherwiseEmpty()
//                .compose(records -> {
//                    if (records == null) {
//                        return client
//                                .preparedQuery("INSERT INTO cart VALUES ($1, $2, $3)")
//                                .execute(Tuple.of(cart.cid(), cart.uid(), cart.dateCreated()))
//                                .compose(r -> client.close());
//                    }
//                    return Future.succeededFuture();
//                })
//                .compose(r -> client.close())
//                .compose(r -> {
//                    cart.items().iterator().forEachRemaining(cartItem -> client
//                            .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4)")
//                            .execute(Tuple.of(cartItem.itemId(), cart.cid(), cartItem.pid(), cartItem.quantity()))
//                            .compose(r2 -> client.close()));
//                    return Future.succeededFuture();
//                });
    }

    @Override
    public Future<Cart> findCart(UUID uid) {
        return pgPool
                .preparedQuery("SELECT c.cid, c.uid, c.datecreated, ci.itemid, ci.pid, ci.quantity " +
                        "FROM cart c LEFT JOIN cartitem ci ON c.cid = ci.cid " +
                        "WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .compose(records -> {
                    ArrayList<CartItem> items = new ArrayList<>();
                    if (records.iterator().next().getUUID("itemid") != null) {
                        records.forEach(row -> {
                            CartItem item = new CartItem(
                                    row.getUUID("itemid"),
                                    row.getUUID("pid"),
                                    row.getInteger("quantity"));
                            items.add(item);
                        });
                    }
                    Row row = records.iterator().next();
                    Cart cart = new Cart(
                            row.getUUID("cid"),
                            row.getUUID("uid"),
                            row.getLocalDateTime("datecreated"),
                            items);
                    return Future.succeededFuture(cart);
                });
    }

    @Override
    public Future<ArrayList<Cart>> findCarts() {
        return pgPool
                .preparedQuery("SELECT * FROM cart;")
                .execute()
                .compose(rows -> {
                    ArrayList<Cart> carts = new ArrayList<>();
                    rows.forEach(row -> {
                        ArrayList<CartItem> cartItems = new ArrayList<>();
                        carts.add(new Cart(
                                row.getUUID("cid"),
                                row.getUUID("uid"),
                                row.getLocalDateTime("datecreated"),
                                cartItems));

                    });
                    return Future.succeededFuture(carts);
                });
    }

    // TODO: 6/7/23 WITH ANADROMI
    public Future<ArrayList<Cart>> findNext(RowIterator iterator, ArrayList<Cart> carts, int position) {
        return null;
    }

    @Override
    public Future<Void> deleteCart(UUID uid) {
        return findCart(uid)
                .compose(cart -> {
                    if (cart != null) {
                        return pgPool
                                .preparedQuery("DELETE FROM cart WHERE uid = $1")
                                .execute(Tuple.of(uid))
                                .compose(v2 -> Future.succeededFuture());
                    }
                    else
                        return Future.failedFuture(new IllegalArgumentException("you don't have any items in your cart"));
                });
    }

    @Override
    public Future<UUID> update(Cart cart) {
        return pgPool
                .preparedQuery("SELECT cid FROM cart WHERE uid = $1")
                .execute(Tuple.of(cart.uid()))
                        .compose(r1 -> deleteNext(cart, 0)
                                .compose(r2 -> updateNext(cart, 0)
                                        .compose(r3 -> pgPool
                                                .preparedQuery("SELECT itemid " +
                                                        "FROM cartitem JOIN cart c2 ON cartitem.cid = c2.cid " +
                                                        "WHERE uid = $1")
                                                .execute(Tuple.of(cart.uid()))
                                                .compose(records -> {
                                                    try {
                                                        System.out.println("item: "+ records.iterator().next().getUUID("itemid"));
                                                        return Future.succeededFuture(records.iterator().next().getUUID("itemid"));
                                                    } catch (Exception e) {
                                                        return Future.succeededFuture();
                                                    }

                                                }))));
    }

    public Future<Void> deleteNext(Cart cart, int position) {
        CartItem cartItem = cart.items().get(position);
        return pgPool
                .preparedQuery("DELETE FROM cartitem WHERE pid = $1 AND cid = $2")
                .execute(Tuple.of(cartItem.pid(), cart.cid()))
                .compose(r -> {
                            if (position+1 < cart.items().size())
                                return deleteNext(cart, position+1);
                            else
                                return Future.succeededFuture();
                        });
    }

    public Future<Void> updateNext(Cart cart, int position) {
        CartItem cartItem = cart.items().get(position);
        return pgPool
                .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                .execute(Tuple.of(cartItem.pid()))
                .compose(rows -> {
                    int stock = rows.iterator().next().getInteger("quantity");

                    // if the quantity to update is higher than the stock then update with the highest amount (stock)
                    if (cartItem.quantity() > stock) {
                        if (stock > 0)
                            return pgPool
                                    .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4);")
                                    .execute(Tuple.of(cartItem.itemId(), cart.cid(), cartItem.pid(), stock))
                                    .compose(r -> {
                                                if (position+1 < cart.items().size())
                                                    return updateNext(cart, position+1);
                                                else
                                                    return Future.succeededFuture();
                                            });
                        else {
                            if (position + 1 < cart.items().size())
                                return updateNext(cart, position + 1);
                            else
                                return Future.succeededFuture();
                        }
                    }

                    else if (cartItem.quantity() > 0)
                        return pgPool
                                .preparedQuery("INSERT INTO cartitem VALUES ($1, $2, $3, $4);")
                                .execute(Tuple.of(cartItem.itemId(), cart.cid(), cartItem.pid(), cartItem.quantity()))
                                .compose(r -> {
                                            if (position+1 < cart.items().size())
                                                return updateNext(cart, position+1);
                                            else
                                                return Future.succeededFuture();
                                        });

                    else
                        return pgPool
                            .preparedQuery("DELETE FROM cartitem WHERE itemid = $1;")
                            .execute(Tuple.of(cartItem.itemId()))
                            .compose(r -> {
                                        if (position+1 < cart.items().size())
                                            return updateNext(cart, position+1);
                                        else
                                            return Future.succeededFuture();
                                    });
                });
    }

}
