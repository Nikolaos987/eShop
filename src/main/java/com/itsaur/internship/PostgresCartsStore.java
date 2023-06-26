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
    public Future<JsonObject> getProduct(UUID productId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE productid = $1;")
                .execute(Tuple.of(productId))
                .compose(rows -> {
                    Row row = rows.iterator().next();
                    return productAsJson(row).compose(jsonProduct -> client.close()
                            .compose(r -> Future.succeededFuture(jsonProduct)));
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<Void> checkQuantity(UUID id, int quantity) {
        if (quantity > 0) {
            SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
            return client
                    .preparedQuery("SELECT * FROM product WHERE productid = $1")
                    .execute(Tuple.of(id))
                    .compose(rows -> {
                        Row row = rows.iterator().next();
                        if (row.getInteger("quantity") - quantity < 0) {
                            return client.close()
                                    .compose(r -> Future.failedFuture(new IllegalArgumentException("out of stock")));
                        } else {
                            return client.close()
                                    .compose(r -> Future.succeededFuture());
                        }
                    });
        } else {
            return Future.failedFuture(new IllegalArgumentException("quantity can not be 0 or less"));
        }
    }

    @Override
    public Future<Void> addToCart(User user, UUID pid, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return findInCart(user, pid)
                .compose(found -> getUserId(user.username(), client).compose(userid -> {
                    if (found) {
                        return client
                                .preparedQuery("SELECT quantity FROM cart WHERE pid=$1 AND uid=$2")
                                .execute(Tuple.of(pid, userid))
                                .compose(row -> {
                                    int cartQuantity = row.iterator().next().getInteger("quantity");
                                    return checkQuantity(pid, cartQuantity + quantity);
                                })
                                .compose(res -> getPrice(pid).compose(price -> client
                                                .preparedQuery("UPDATE cart SET quantity = quantity + $2, price = price + ($3 * $2) WHERE pid = $1 AND uid = $4;")
                                                .execute(Tuple.of(pid, quantity, price, userid)))
                                        .compose(v -> client.close())
                                        .compose(v -> Future.succeededFuture()));
                    } else {
                        return client
                                .preparedQuery("INSERT INTO cart (pid, uid, username, name, price, quantity) SELECT productid, $3, $4, name, price * $2, $2 FROM product WHERE productid = $1")
                                .execute(Tuple.of(pid, quantity, userid, user.username()))
                                .compose(r -> client.close())
                                .compose(r -> Future.succeededFuture());
                    }
                }));
    }

    @Override
    public Future<Boolean> findInCart(User user, UUID id) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT pid FROM cart WHERE pid = $1 AND username = $2")
                .execute(Tuple.of(id, user.username()))
                .compose(rows -> {
                    if (rows.size() != 0)
                        return client.close()
                                .compose(r -> Future.succeededFuture(true));
                    else
                        return client.close()
                                .compose(r -> Future.succeededFuture(false));
                });
    }

    @Override
    public Future<Void> removeFromCart(User user, UUID productId, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return getUserId(user.username(), client)
                .compose(userid -> client
                        .preparedQuery("SELECT quantity FROM cart WHERE uid = $1 AND pid = $2")
                        .execute(Tuple.of(userid, productId))
                        .compose(rows -> {
                            int cartProductQuantity = rows.iterator().next().getInteger("quantity");
                            if (cartProductQuantity - quantity > 0) {
                                return getPrice(productId).compose(price -> client
                                        .preparedQuery("UPDATE cart SET quantity = quantity - $1, price = price - ($1 * $4) WHERE uid = $2 AND pid = $3")
                                        .execute(Tuple.of(quantity, userid, productId, price))
                                        .compose(v -> client.close())
                                        .compose(v -> Future.succeededFuture()));
                            } else {
                                return client
                                        .preparedQuery("DELETE FROM cart WHERE uid = $1 AND pid = $2")
                                        .execute(Tuple.of(userid, productId))
                                        .compose(v -> client.close())
                                        .compose(v -> Future.succeededFuture());
                            }
                        }));
    }

    @Override
    public Future<JsonArray> cart(UUID cid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT pid, name, price, quantity FROM cart WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(rows -> {
                    JsonArray cartProducts = new JsonArray();
                    rows.forEach(row -> cartProducts.add(JsonObject.of(
                            "PRODUCT ID", row.getUUID("pid"),
                            "NAME", row.getString("name"),
                            "PRICE", row.getDouble("price"),
                            "QUANTITY", row.getInteger("quantity"))));
                    if (cartProducts.size() != 0)
                        return totalPrice(username)
                                .onSuccess(val -> cartProducts.add(JsonObject.of("TOTAL PRICE", val)))
                                .compose(v -> client.close())
                                .compose(v -> Future.succeededFuture(cartProducts));
                    return Future.failedFuture(new IllegalArgumentException("Seems like your cart is empty"));
                });
    }

    @Override
    public Future<Void> buy(UUID cid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return getUserId(cid, client)
                .compose(this::removeQuantity)
                .compose(v -> client.preparedQuery("DELETE FROM cart WHERE USERNAME = $1")
                        .execute(Tuple.of(cid))
                        .compose(r -> client.close())
                        .compose(r -> Future.succeededFuture()));
    }



    public Future<UUID> getUserId(String username, SqlClient client) {
        return client
                .preparedQuery("SELECT uid FROM users WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(useridRow -> {
                    UUID userid = useridRow.iterator().next().getUUID("userid");
                    return Future.succeededFuture(userid);
                });
    }

    public Future<Void> removeQuantity(UUID userid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT pid, quantity FROM cart WHERE uid = $1")
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

    public Future<Double> totalPrice(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT SUM(price) FROM cart WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(res -> {
                    double totalPrice = res.iterator().next().getDouble("sum");
                    return client.close()
                            .compose(r -> Future.succeededFuture(totalPrice));
                });
    }

    public Future<Void> removeNext(ArrayList<UUID> ids, ArrayList<Integer> quantities, int pos, SqlClient client) {
        if (pos < ids.size()) {
            return client
                    .preparedQuery("UPDATE product SET quantity = quantity - $2 WHERE productid = $1;")
                    .execute(Tuple.of(ids.get(pos), quantities.get(pos)))
                    .compose(rows -> removeNext(ids, quantities, pos+1, client));
        } return client.close()
                .compose(r -> Future.succeededFuture());
    }

    public Future<Double> getPrice(UUID productId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT price FROM product WHERE productid = $1")
                .execute(Tuple.of(productId))
                .compose(p -> {
                    double price = p.iterator().next().getDouble("price");
                    return client.close()
                            .compose(r -> Future.succeededFuture(price));
                });
    }

    Future<JsonObject> productAsJson(Row row) {
        JsonObject jsonProduct = new JsonObject();
        Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
        jsonProduct.put("PRODUCT ID", product.productId());
        jsonProduct.put("NAME", product.name());
        jsonProduct.put("DESCRIPTION", product.description());
        jsonProduct.put("PRICE", product.price());
        jsonProduct.put("QUANTITY", product.quantity());
        jsonProduct.put("BRAND", product.brand());
        jsonProduct.put("CATEGORY", product.category());
        return Future.succeededFuture(jsonProduct);
    }
}
