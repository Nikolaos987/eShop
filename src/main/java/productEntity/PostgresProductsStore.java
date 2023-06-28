package productEntity;

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
    public Future<JsonObject> getProduct(UUID productId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(productId))
                .compose(rows -> {
                    Row row = rows.iterator().next();
                    return productAsJson(row).compose(jsonProduct -> client.close()
                            .compose(r -> Future.succeededFuture(jsonProduct)));
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
                    rows.forEach(row -> productAsJson(row).onSuccess(products::add));
                    return client.close()
                            .compose(r -> Future.succeededFuture(products));
                });
    }

    @Override
    public Future<JsonArray> filter(double price, String brand, String category) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE price <= $1 AND brand = $2 AND category = $3")
                .execute(Tuple.of(price, brand, category))
                .compose(rows -> {
                    JsonArray products = new JsonArray();
                    rows.forEach(row -> productAsJson(row).onSuccess(products::add));
                    return client.close()
                            .compose(r -> Future.succeededFuture(products));
                });
    }

    @Override
    public Future<Void> create(String name, String description, double price, int quantity, String brand, String category) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
            return client
                    .preparedQuery("INSERT INTO product (name, description, price, quantity, brand, category) VALUES ($1, $2, $3, $4, $5, $6);")
                    .execute(Tuple.of(name, description, price, quantity, brand, category))
                    .compose(res -> Future.succeededFuture());
    }

    Future<JsonObject> productAsJson(Row row) {
        JsonObject jsonProduct = new JsonObject();
        Product product = new Product(row.getUUID("pid"), row.getString("name"), row.getString("description"), row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"), row.getString("category"));
        jsonProduct.put("PRODUCT ID", product.pid());
        jsonProduct.put("NAME", product.name());
        jsonProduct.put("DESCRIPTION", product.description());
        jsonProduct.put("PRICE", product.price());
        jsonProduct.put("QUANTITY", product.quantity());
        jsonProduct.put("BRAND", product.brand());
        jsonProduct.put("CATEGORY", product.category());
        return Future.succeededFuture(jsonProduct);
    }
}
