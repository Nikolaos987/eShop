package productEntity;

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
                .preparedQuery("INSERT INTO product (name, description, price, quantity, brand, category) VALUES ($1, $2, $3, $4, $5, $6);")
                .execute(Tuple.of(product.name(), product.description(), product.price(), product.quantity(), product.brand(), product.category()))
                .compose(res -> Future.succeededFuture());
    }

    @Override
    public Future<Product> findProduct(UUID productId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(productId))
                .compose(records -> {
                    Row row = records.iterator().next();
                    Product product = new Product(row.getUUID("pid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
                    return Future.succeededFuture(product);
                })
                .otherwiseEmpty();
    }

    @Override
    public Future<Collection<Product>> findProduct(String name) {
        name = "%"+name+"%";
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE name LIKE $1;")
                .execute(Tuple.of(name))
                .compose(rows -> {
                    Collection<Product> products = new ArrayList<>();
                    rows.forEach(row -> products.add(new Product(row.getUUID("pid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"))));
                    return client.close()
                            .compose(r -> Future.succeededFuture(products));
                });
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
    public Future<Void> updateProduct(UUID pid, double price) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE product SET price = $2 WHERE pid = $1")
                .execute(Tuple.of(pid, price))
                .compose(records -> Future.succeededFuture());
    }

}
