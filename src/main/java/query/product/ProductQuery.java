package query.product;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import productEntity.Category;
import productEntity.Product;
import query.cart.CartQueryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ProductQuery implements ProductQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public ProductQuery(int port, String host, String db, String user, String password, int poolSize) {
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
    public Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                            row.getUUID("pid"), row.getString("name"), row.getString("description"),
                            row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                            Category.valueOf(row.getString("category")));
                    return Future.succeededFuture(product);
                });
    }

    @Override
    public Future<ProductsQueryModel> findProductsByName(String regex) {
            regex = "%"+regex+"%";
            SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
            return client
                    .preparedQuery("SELECT * FROM product WHERE name LIKE $1;")
                    .execute(Tuple.of(regex))
                    .compose(rows -> {
                        List<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                        rows.forEach(row -> {
                            ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                    row.getUUID("pid"), row.getString("name"), row.getString("description"),
                                    row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                                    Category.valueOf(row.getString("category")));
                            products.add(product);
                        });
                        ProductsQueryModel productList = new ProductsQueryModel(products);
                        return client.close()
                                .compose(r -> Future.succeededFuture(productList));
                    });
    }

    @Override
    public Future<ProductsQueryModel> findProducts() {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM product;")
                .execute()
                .compose(rows -> {
                    List<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"), row.getString("name"), row.getString("description"),
                                row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
                    ProductsQueryModel productList = new ProductsQueryModel(products);
                    return client.close()
                            .compose(r -> Future.succeededFuture(productList));
                });
    }
}
