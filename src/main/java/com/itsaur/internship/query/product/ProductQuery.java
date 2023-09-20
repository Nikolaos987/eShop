package com.itsaur.internship.query.product;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import com.itsaur.internship.productEntity.Category;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class ProductQuery implements ProductQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public ProductQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Future<Integer> productsCount() {
        return pgPool
                .query("SELECT COUNT(pid) FROM product")
                .execute()
                .compose(records -> Future.succeededFuture(records.iterator().next().getInteger(0)));
    }

    @Override
    public Future<Integer> filteredProductsCount(String regex) {
        regex = "%" + regex + "%".toLowerCase();
        return pgPool
                .preparedQuery("SELECT COUNT(pid) FROM product WHERE LOWER(name) LIKE $1")
                .execute(Tuple.of(regex))
                .compose(records -> Future.succeededFuture(records.iterator().next().getInteger(0)));
    }

    @Override
    public Future<Integer> productsCategoryCount(String category) {
        return pgPool
                .preparedQuery("SELECT COUNT(pid) FROM product WHERE category = $1")
                .execute(Tuple.of(category))
                .compose(records -> Future.succeededFuture(records.iterator().next().getInteger(0)));
    }

    @Override
    public Future<Integer> productsCategoriesCount(String[] category) {
        return pgPool
                .preparedQuery("SELECT COUNT(pid) FROM product WHERE category = any ($1)")
                .execute(Tuple.of(category))
                .compose(records -> Future.succeededFuture(records.iterator().next().getInteger(0)));
    }

    @Override
    public Future<Integer> productsFilteredCategoriesCount(String regex, String[] category) {
        regex = "%" + regex + "%".toLowerCase();
        return pgPool
                .preparedQuery("SELECT COUNT(pid) FROM product WHERE LOWER(name) LIKE $2 AND category = any ($1);")
                .execute(Tuple.of(category, regex))
                .compose(records -> Future.succeededFuture(records.iterator().next().getInteger(0)));
    }

    @Override
    public Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE pid = $1;")
                .execute(Tuple.of(pid))
                .compose(records -> {
                    try {
                        Row row = records.iterator().next();
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"), row.getString("name"), row.getString("description"),
                                row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        return Future.succeededFuture(product);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture("Product not found");
                    }

                });
    }

    @Override
    public Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex, int from, int range) {
        regex = "%" + regex + "%".toLowerCase();
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE LOWER(name) LIKE $1 LIMIT $3 OFFSET $2;")
                .execute(Tuple.of(regex, from, range))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
//                                row.getString("image"),
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
    public Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByCategory(String category, int from, int range) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE category = $3 LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range, category))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
                                row.getString("description"),
                                row.getDouble("price"),
                                row.getInteger("quantity"),
                                row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByCategories(String[] category, int from, int range) {
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE category = any ($3) LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range, category))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
                                row.getString("description"),
                                row.getDouble("price"),
                                row.getInteger("quantity"),
                                row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<List<ProductsQueryModel.ProductQueryModel>> findFilteredProductsByCategories(String regex, String[] category, int from, int range) {
        regex = "%" + regex + "%".toLowerCase();
        return pgPool
                .preparedQuery("SELECT * FROM product WHERE LOWER(name) LIKE $2 AND category = any ($1) LIMIT $4 OFFSET $3;")
                .execute(Tuple.of(category, regex, from, range))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
                                row.getString("description"),
                                row.getDouble("price"),
                                row.getInteger("quantity"),
                                row.getString("brand"),
                                Category.valueOf(row.getString("category")));
                        products.add(product);
                    });
                    return Future.succeededFuture(products);
                });
    }

    @Override
    public Future<List<ProductsQueryModel.ProductQueryModel>> findProducts(int from, int range) {
        return pgPool
                .preparedQuery("SELECT * FROM product LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range))
                .compose(rows -> {
                    ArrayList<ProductsQueryModel.ProductQueryModel> products = new ArrayList<>();
                    rows.forEach(row -> {
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"),
                                row.getString("name"),
//                                row.getString("image"),
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
                    return vertx.fileSystem().readFile("/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/assets/" + pid + ".jpeg")
                            .compose(file -> {
                                Buffer buffer = Buffer.buffer(file.getBytes());
                                return Future.succeededFuture(buffer);
                            });

//                    return Future.succeededFuture(records.iterator().next().getString("image"));
    }


}
