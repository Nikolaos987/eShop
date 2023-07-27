package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartItem;
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

public class PostgresProductsStore implements ProductsStore {

    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public PostgresProductsStore(int port, String host, String db, String user, String password, int poolSize) {
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
    public Future<Void> insert(Product product) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("INSERT INTO product (name, image, description, price, quantity, brand, category) VALUES ($1, $2, $3, $4, $5, $6, $7);")
                .execute(Tuple.of(product.name(), product.image(), product.description(), product.price(), product.quantity(), product.brand(), product.category()))
                .compose(res -> Future.succeededFuture());
    }

    @Override
    public Future<Product> findProduct(UUID pid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    Product product = new Product(
                            row.getUUID("pid"),
                            row.getString("name"),
                            row.getString("image"),
                            row.getString("description"),
                            row.getDouble("price"),
                            row.getInteger("quantity"),
                            row.getString("brand"),
                            Category.valueOf(row.getString("category")));
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    // TODO: 7/7/23 DELETE
    @Override
    public Future<Product> findProduct(String name) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE name = $1;")
                .execute(Tuple.of(name))
                .compose(records -> {
                    Row row = records.iterator().next();
                    Product product = new Product(
                            row.getUUID("pid"),
                            row.getString("name"),
                            row.getString("image"),
                            row.getString("description"),
                            row.getDouble("price"),
                            row.getInteger("quantity"),
                            row.getString("brand"),
                            Category.valueOf(row.getString("category")));
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<Void> deleteProduct(UUID pid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("DELETE FROM product WHERE pid = $1")
                .execute(Tuple.of(pid))
                .compose(records -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateProduct(Product product) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE product SET name = $1, image = $2, description = $3, price = $4, quantity = $5, brand = $6, category = $7  WHERE pid = $8")
                .execute(Tuple.of(product.name(), product.image(), product.description(), product.price(), product.quantity(), product.brand(), product.category(), product.pid()))
                .compose(records -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateProducts(ArrayList<CartItem> items) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
//        return client
//                .preparedQuery("UPDATE product SET quantity = $2 WHERE pid = $1")
//                .execute(Tuple.of())
//                .compose(records -> Future.succeededFuture());

        return updateNext(items, 0)
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateProducts(UUID uid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
//                .preparedQuery("UPDATE product SET quantity = quantity - (SELECT quantity FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE uid = $1);")
                .preparedQuery("SELECT pid FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .onSuccess(records -> records.forEach(row -> client
                        .preparedQuery("UPDATE product SET quantity = quantity - (SELECT quantity FROM cartitem JOIN cart ON cartitem.cid = cart.cid WHERE pid = $1 AND uid = $2) WHERE pid = $1")
                        .execute(Tuple.of(row.getUUID("pid"), uid))
                        .compose(recs -> Future.succeededFuture())))
                .compose(res -> Future.succeededFuture());
    }

    public Future<Void> updateNext(ArrayList<CartItem> items, int position) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return inStock(items.get(position).pid(), items.get(position).quantity()).compose(result -> client
                .preparedQuery("UPDATE product SET quantity = quantity - $1 WHERE pid = $2")
                .execute(Tuple.of(items.get(position).quantity(), items.get(position).pid()))
                .compose(records2 -> {
                    if (position + 1 < items.size())
                        return updateNext(items, position + 1);
                    return Future.succeededFuture();
                }));
    }

    public Future<Void> inStock(UUID pid, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT quantity FROM product WHERE pid = $1")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    if (row.getInteger("quantity") - quantity >= 0)
                        return Future.succeededFuture();
                    return Future.failedFuture("cannot buy such amount. Out of stock");
                });
    }
}
