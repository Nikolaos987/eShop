package com.itsaur.internship.query.product;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import com.itsaur.internship.productEntity.Category;

import java.util.ArrayList;
import java.util.UUID;

public class ProductQuery implements ProductQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public ProductQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                            row.getUUID("pid"), row.getString("name"), row.getString("image"), row.getString("description"),
                            row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                            Category.valueOf(row.getString("category")));
                    return Future.succeededFuture(product);
                });
    }

    @Override
    public Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex) {
        regex = "%" + regex + "%".toLowerCase();
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE LOWER(name) LIKE $1;")
                .execute(Tuple.of(regex))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
                                row.getString("image"),
                                row.getString("description"),
                                row.getDouble("price"),
                                row.getInteger("quantity"),
                                row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
//                        ProductsQueryModel productList = new ProductsQueryModel(products);
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProducts(int from, int range) {
        return pgPool
                .preparedQuery("SELECT * FROM product LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
                                row.getString("image"),
                                row.getString("description"),
                                row.getDouble("price"),
                                row.getInteger("quantity"),
                                row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
//                    ProductsQueryModel productList = new ProductsQueryModel(products);
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<Buffer> findImageById(UUID pid) {
        return pgPool
                .preparedQuery("SELECT image FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    Row row = records.iterator().next();
                    return vertx.fileSystem().readFile(row.getString("image"))
                            .compose(file -> {
                                Buffer buffer = Buffer.buffer(file.getBytes());
                                return Future.succeededFuture(buffer);
                            });

//                    return Future.succeededFuture(records.iterator().next().getString("image"));
                });
    }


}
