package com.itsaur.internship;

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

public class PostgresProductStore implements ProductsStore{
    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public PostgresProductStore(int port, String host, String db, String user, String password, int poolSize) {
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
                    return Future.succeededFuture(products);
                });
    }

    // TODO: 19/6/23 (nikos): add column price and calculate the total price for all products in cart
    @Override
    public Future<Void> addToCart(UUID id, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions );
        String addToCartQuery = "INSERT INTO cart (pid, name, quantity) SELECT productid, name, $2 FROM product WHERE productid = $1";
        return client
                .preparedQuery(addToCartQuery)
                .execute(Tuple.of(id, quantity))
                .compose(rows -> Future.succeededFuture());
    }

    @Override
    public Future<Void> removeQuantity(UUID id, int quantity) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions );
        String removeQuantityQuery = "UPDATE product SET quantity = quantity - $1 WHERE productid = $2;";
        return client
                .preparedQuery(removeQuantityQuery)
                .execute(Tuple.of(quantity, id))
                .compose(rows -> Future.succeededFuture());
    }

}
