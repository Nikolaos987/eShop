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
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostgresUsersStore implements UsersStore {
    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public PostgresUsersStore(int port, String host, String db, String user, String password, int poolSize) {
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
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String insertQuery = "INSERT INTO customer VALUES ($1, $2, $3);";
        return client
                .preparedQuery(insertQuery)
                .execute(Tuple.of(UUID.randomUUID(), user.username(), user.password()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<User> findUser(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String findQuery = "SELECT * FROM customer WHERE username = $1";
        return client
                .preparedQuery(findQuery)
                .execute(Tuple.of(username))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getString("username"), row.getString("password"));
                        client.close();
                        return Future.succeededFuture(user);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    public Future<Void> logoutUser() {
        return User.forget()
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<User> checkLoggedIn() {
        String usr = null;
        String pass = null;
        if (User.pref.get("username", usr) != null) {
            String username = User.pref.get("username", usr);
            String password = User.pref.get("password", pass);
            User user = new User(username, password);
            return Future.succeededFuture(user);
        }
        return Future.failedFuture(new IllegalArgumentException("you are not logged in"));
    }

    @Override
    public Future<Void> deleteUser(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String deleteQuery = "DELETE FROM customer WHERE username = $1;";
        return client
                .preparedQuery(deleteQuery)
                .execute(Tuple.of(user.username()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String updateQuery = "UPDATE customer SET password = $2 WHERE username = $1;";
        return client
                .preparedQuery(updateQuery)
                .execute(Tuple.of(username, password))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<JsonObject> getProduct(String name) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String searchQuery = "SELECT * FROM product WHERE name = $1;";
        return client
                .preparedQuery(searchQuery)
                .execute(Tuple.of(name))
                .compose(rows -> {
                    JsonObject jsonProduct = new JsonObject();
                    Row row = rows.iterator().next();

                    Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                    jsonProduct.put("PRODUCT ID", product.productId());
                    jsonProduct.put("NAME", product.name());
                    jsonProduct.put("DESCRIPTION", product.description());
                    jsonProduct.put("PRICE", product.price());
                    jsonProduct.put("QUANTITY", product.quantity());
                    jsonProduct.put("BRAND", product.brand());
                    jsonProduct.put("CATEGORY", product.category());

                    client.close();
                    return Future.succeededFuture(jsonProduct);
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<JsonArray> findProducts(String name) {
        name = "%"+name+"%";
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE name LIKE $1;")
                .execute(Tuple.of(name))
                .compose(rows -> {
                    JsonArray products = new JsonArray();
                    rows.forEach(row -> {
                        JsonObject jsonProduct = new JsonObject();
                        Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                        jsonProduct.put("PRODUCT ID", product.productId());
                        jsonProduct.put("NAME", product.name());
                        jsonProduct.put("DESCRIPTION", product.description());
                        jsonProduct.put("PRICE", product.price());
                        jsonProduct.put("QUANTITY", product.quantity());
                        jsonProduct.put("BRAND", product.brand());
                        jsonProduct.put("CATEGORY", product.category());

                        products.add(product);
                    });
                    client.close();
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<JsonArray> filter(double price, String brand, String category) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String filterQuery = "SELECT * FROM product WHERE price <= $1 AND brand = $2 AND category = $3";
        return client
                .preparedQuery(filterQuery)
                .execute(Tuple.of(price, brand, category))
                .compose(rows -> {
                    JsonArray products = new JsonArray();
                    rows.forEach(row -> {
                        JsonObject jsonProduct = new JsonObject();
                        Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                        jsonProduct.put("PRODUCT ID", product.productId());
                        jsonProduct.put("NAME", product.name());
                        jsonProduct.put("DESCRIPTION", product.description());
                        jsonProduct.put("PRICE", product.price());
                        jsonProduct.put("QUANTITY", product.quantity());
                        jsonProduct.put("BRAND", product.brand());
                        jsonProduct.put("CATEGORY", product.category());

                        products.add(product);
                    });
                    client.close();
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<Void> addToCart(User user, UUID pid, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return findInCart(user, pid)
                .compose(found -> getUserId(user.username(), client).compose(userid -> {
                    if (found) {
                        return getPrice(pid).compose(price -> client
                                        .preparedQuery("UPDATE cart SET quantity = quantity + $2, price = price + ($3 * $2) WHERE pid = $1 AND uid = $4;")
                                        .execute(Tuple.of(pid, quantity, price, userid)).compose(r -> Future.succeededFuture())
                                        .compose(v -> Future.succeededFuture()));
                    } else {
                        return client
                                .preparedQuery("INSERT INTO cart (pid, uid, username, name, price, quantity) SELECT productid, $3, $4, name, price * $2, $2 FROM product WHERE productid = $1")
                                .execute(Tuple.of(pid, quantity, userid, user.username())).compose(r -> Future.succeededFuture());
                    }
                }));
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
                                        .compose(v -> Future.succeededFuture()));
                            } else {
                                return client
                                        .preparedQuery("DELETE FROM cart WHERE uid = $1 AND pid = $2")
                                        .execute(Tuple.of(userid, productId))
                                        .compose(v -> Future.succeededFuture());
                            }
                        }));
    }

    @Override
    public Future<JsonArray> cart(String username) {
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
                                .compose(v -> Future.succeededFuture(cartProducts));
                    return Future.failedFuture(new IllegalArgumentException("Seems like your cart is empty"));
                });
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
                            return Future.failedFuture(new IllegalArgumentException("out of stock"));
                        } else {
                            return Future.succeededFuture();
                        }
                    });
        } else {
            return Future.failedFuture(new IllegalArgumentException("quantity can not be 0 or less"));
        }
    }

    @Override
    public Future<Void> buy(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return getUserId(username, client)
                .compose(this::removeQuantity)
                .compose(v -> client.preparedQuery("DELETE FROM cart WHERE USERNAME = $1")
                        .execute(Tuple.of(username))
                        .compose(r -> Future.succeededFuture()));


    }

    public Future<Double> getPrice(UUID productId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT price FROM product WHERE productid = $1")
                .execute(Tuple.of(productId))
                .compose(p -> {
                    double price = p.iterator().next().getDouble("price");
                    return Future.succeededFuture(price);
                });
    }

    public Future<Double> totalPrice(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT SUM(price) FROM cart WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(res -> {
                    double totalPrice = res.iterator().next().getDouble("sum");
                    return Future.succeededFuture(totalPrice);
                });
    }


    public Future<UUID> getUserId(String username, SqlClient client) {
        return client
                .preparedQuery("SELECT userid FROM customer WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(useridRow -> {
                    UUID userid = useridRow.iterator().next().getUUID("userid");
                    return Future.succeededFuture(userid);
                });
    }
    @Override
    public Future<Boolean> findInCart(User user, UUID id) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT pid FROM cart WHERE pid = $1 AND username = $2")
                .execute(Tuple.of(id, user.username()))
                .compose(rows -> {
                    if (rows.size() != 0)
                        return Future.succeededFuture(true);
                    else
                        return Future.succeededFuture(false);
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

    public Future<Void> removeNext(ArrayList<UUID> ids, ArrayList<Integer> quantities, int pos, SqlClient client) {
        if (pos < ids.size()) {
            return client
                    .preparedQuery("UPDATE product SET quantity = quantity - $2 WHERE productid = $1;")
                    .execute(Tuple.of(ids.get(pos), quantities.get(pos)))
                    .compose(rows -> removeNext(ids, quantities, pos+1, client));
        } return Future.succeededFuture();
    }
}
