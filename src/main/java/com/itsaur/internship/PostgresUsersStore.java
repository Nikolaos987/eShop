package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

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
    public Future<Product> findProduct(String name) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions );
        String searchQuery = "SELECT * FROM product WHERE name = $1;";
        return client
                .preparedQuery(searchQuery)
                .execute(Tuple.of(name))
                .compose(rows -> {
                    Row row = rows.iterator().next();
                    Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                    client.close();
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<ArrayList<Product>> filter(double price, String category) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions );
        String filterQuery = "SELECT * FROM product WHERE price <= $1 AND category = $2;";
        return client
                .preparedQuery(filterQuery)
                .execute(Tuple.of(price, category))
                .compose(rows -> {
                    ArrayList<Product> products = new ArrayList<>();
                    rows.forEach(row -> {
                        Product product = new Product(row.getUUID("productid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                        products.add(product);
                    });
                    client.close();
                    products.forEach(product -> {
                        System.out.println("ID:\t\t\t " + product.productId() +
                                "\nname:\t\t " + product.name() +
                                "\ndescription: " + product.description() +
                                "\nbrand:\t\t " + product.brand() +
                                "\nprice:\t\t " + product.price() +
                                "\nquantity:\t " + product.quantity() +
                                "\ncategory:\t " + product.category());
                        System.out.println();
                    });
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<Void> addToCart(UUID id, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return removeQuantity(id, quantity)
                .compose(res -> findInCart(id, client)
                        .compose(found -> {
                            if (found) {
                                return client
                                        .preparedQuery("SELECT price FROM product WHERE productid = $1")
                                        .execute(Tuple.of(id))
                                        .compose(price -> {
                                            float p = price.iterator().next().getFloat("price");
                                            return client
                                                    .preparedQuery("UPDATE cart SET quantity = quantity + $2, price = price + $3 * $2 WHERE pid = $1;")
                                                    .execute(Tuple.of(id, quantity, p)).compose(r -> Future.succeededFuture());
                                        });
                            } else {
                                return client
                                        .preparedQuery("INSERT INTO cart (pid, name, price, quantity) SELECT productid, name, price * $2, $2 FROM product WHERE productid = $1")
                                        .execute(Tuple.of(id, quantity)).compose(r -> Future.succeededFuture());
                            }
                        }));
    }

    public Future<Boolean> findInCart(UUID id, SqlClient client) {
        return client
                .preparedQuery("SELECT pid FROM cart WHERE pid = $1")
                .execute(Tuple.of(id))
                .compose(rows -> {
                    if (rows.size() != 0) {
                        return Future.succeededFuture(true);
                    } else
                        return Future.succeededFuture(false);
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

    // TODO: 20/6/23 remove products from cart after buying them
    @Override
    public Future<Void> buy() {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT SUM(price) FROM cart")
                .execute()
                .compose(res -> {
                    float totalPrice = res.iterator().next().getFloat("sum");
                    System.out.println("total price = $" + totalPrice);
                    return Future.succeededFuture();
                });
    }

    public Future<Void> removeQuantity(UUID id, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String removeQuantityQuery = "UPDATE product SET quantity = quantity - $1 WHERE productid = $2;";
        return client
                .preparedQuery(removeQuantityQuery)
                .execute(Tuple.of(quantity, id))
                .otherwiseEmpty()
                .compose(rows -> {
                    if (rows == null) {  // when quantity <= 0
                        return Future.failedFuture(new IllegalArgumentException("product is out of stock"));
                    } else {
                        return Future.succeededFuture();
                    }
                });
    }
}
