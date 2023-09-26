package com.itsaur.internship.query.product;

import com.itsaur.internship.productEntity.Product;
import com.itsaur.internship.query.relatedProducts.RelatedProductsQueryModel;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.*;

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
                    try {
                        Row row = records.iterator().next();
                        ProductsQueryModel.ProductQueryModel product = new ProductsQueryModel.ProductQueryModel(
                                row.getUUID("pid"), row.getString("name"), row.getString("description"),
                                row.getDouble("price"), row.getInteger("quantity"), row.getString("brand"),
                                row.getString("category"));
                        return Future.succeededFuture(product);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture("Product not found");
                    }

                });
    }

    @Override
    public Future<ProductsQueryModel> findProductsByName(String regex, int from, int range) {
        regex = "%" + regex + "%".toLowerCase();
        String finalRegex = regex;
        return pgPool
                .preparedQuery("SELECT * , COUNT(*) OVER() as totalcount FROM product WHERE LOWER(name) LIKE $1 LIMIT $3 OFFSET $2;")
                .execute(Tuple.of(finalRegex, from, range))
                .compose(productsRecords -> {
                    List<ProductsQueryModel.ProductQueryModel> productsList = new ArrayList<>();
                    ProductsQueryModel productsQueryModel;
                    try {
                        int count = productsRecords.iterator().next().getInteger("totalcount");
                        productsRecords.forEach(row -> {
                            UUID pid = row.getUUID("pid");
                            String name = row.getString("name");
                            String description = row.getString("description");
                            double price = row.getDouble("price");
                            int quantity = row.getInteger("quantity");
                            String brand = row.getString("brand");
                            String category = row.getString("category");

                            ProductsQueryModel.ProductQueryModel productQueryModel = new ProductsQueryModel
                                    .ProductQueryModel(pid, name, description, price, quantity, brand, category);
                            productsList.add(productQueryModel);
                        });
                        productsQueryModel = new ProductsQueryModel(productsList, count);
                    } catch (Exception e) {
                        return Future.succeededFuture(new ProductsQueryModel(new ArrayList<>(), 0));
                    }
                    return Future.succeededFuture(productsQueryModel);
                });
    }

    @Override
    public Future<ProductsQueryModel> findProductsByCategories(String[] category, int from, int range) {
        return pgPool
                .preparedQuery("SELECT * , COUNT(*) OVER() as totalcount FROM product WHERE category = any ($3) LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range, category))
                .compose(productsRecords -> {
                    List<ProductsQueryModel.ProductQueryModel> productsList = new ArrayList<>();
                    ProductsQueryModel productsQueryModel;
                    try {
                        int count = productsRecords.iterator().next().getInteger("totalcount");
                        productsRecords.forEach(row -> {
                            UUID pid = row.getUUID("pid");
                            String name = row.getString("name");
                            String description = row.getString("description");
                            double price = row.getDouble("price");
                            int quantity = row.getInteger("quantity");
                            String brand = row.getString("brand");
                            String category2 = row.getString("category");

                            ProductsQueryModel.ProductQueryModel productQueryModel = new ProductsQueryModel
                                    .ProductQueryModel(pid, name, description, price, quantity, brand, category2);
                            productsList.add(productQueryModel);
                        });
                        productsQueryModel = new ProductsQueryModel(productsList, count);
                    } catch (Exception e) {
                        return Future.succeededFuture(new ProductsQueryModel(new ArrayList<>(), 0));
                    }
                    return Future.succeededFuture(productsQueryModel);
                });
    }

    @Override
    public Future<ProductsQueryModel> findFilteredProductsByCategories(String regex, String[] category, int from, int range) {
        regex = "%" + regex + "%".toLowerCase();
        String finalRegex = regex;
        return pgPool
                .preparedQuery("SELECT * , COUNT(*) OVER() as totalcount FROM product WHERE LOWER(name) LIKE $2 AND category = any ($1) LIMIT $4 OFFSET $3;")
                .execute(Tuple.of(category, finalRegex, from, range))
                .compose(productsRecords -> {
                    List<ProductsQueryModel.ProductQueryModel> productsList = new ArrayList<>();
                    ProductsQueryModel productsQueryModel;
                    try {
                        int count = productsRecords.iterator().next().getInteger("totalcount");
                        productsRecords.forEach(row -> {
                            UUID pid = row.getUUID("pid");
                            String name = row.getString("name");
                            String description = row.getString("description");
                            double price = row.getDouble("price");
                            int quantity = row.getInteger("quantity");
                            String brand = row.getString("brand");
                            String category2 = row.getString("category");

                            ProductsQueryModel.ProductQueryModel productQueryModel = new ProductsQueryModel
                                    .ProductQueryModel(pid, name, description, price, quantity, brand, category2);
                            productsList.add(productQueryModel);
                        });
                        productsQueryModel = new ProductsQueryModel(productsList, count);
                    } catch (Exception e) {
                        return Future.succeededFuture(new ProductsQueryModel(new ArrayList<>(), 0));
                    }
                    return Future.succeededFuture(productsQueryModel);
                });
    }

    @Override
    public Future<ProductsQueryModel> findProducts(int from, int range) {
        return pgPool
                .preparedQuery("SELECT * , COUNT(*) OVER() as totalcount FROM product LIMIT $2 OFFSET $1;")
                .execute(Tuple.of(from, range))
                .compose(productsRecords -> {
                    List<ProductsQueryModel.ProductQueryModel> productsList = new ArrayList<>();
                    ProductsQueryModel productsQueryModel;
                    try {
                        int count = productsRecords.iterator().next().getInteger("totalcount");
                        productsRecords.forEach(row -> {
                            UUID pid = row.getUUID("pid");
                            String name = row.getString("name");
                            String description = row.getString("description");
                            double price = row.getDouble("price");
                            int quantity = row.getInteger("quantity");
                            String brand = row.getString("brand");
                            String category = row.getString("category");

                            ProductsQueryModel.ProductQueryModel productQueryModel = new ProductsQueryModel
                                    .ProductQueryModel(pid, name, description, price, quantity, brand, category);
                            productsList.add(productQueryModel);
                        });
                        productsQueryModel = new ProductsQueryModel(productsList, count);
                    } catch (Exception e) {
                        return Future.succeededFuture(new ProductsQueryModel(new ArrayList<>(), 0));
                    }
                    return Future.succeededFuture(productsQueryModel);
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

    @Override
    public Future<CategoriesQueryModel> fetchCategories() {
        return pgPool
                .preparedQuery("SELECT DISTINCT category FROM product;")
                .execute()
                .compose(records -> {
                    List<CategoriesQueryModel.CategoryQueryModel> categoriesList = new ArrayList<>();
                    CategoriesQueryModel categoriesQueryModel;
                    try {
                        records.forEach(category -> {
                            String current = category.getString("category");
                            CategoriesQueryModel.CategoryQueryModel categoryQueryModel =
                                    new CategoriesQueryModel.CategoryQueryModel(current);
                            categoriesList.add(categoryQueryModel);
                        });
                        categoriesQueryModel = new CategoriesQueryModel(categoriesList);
                    } catch (Exception e) {
                        return Future.succeededFuture(new CategoriesQueryModel(new ArrayList<>()));
                    }
                    return Future.succeededFuture(categoriesQueryModel);
                });
    }

    @Override
    public Future<List<Product>> getRelatedProducts(UUID r_pid) {
        return pgPool
                .preparedQuery("SELECT * " +
                        "FROM related_products rp JOIN product p on p.pid = rp.to_pid " +
                        "WHERE r_pid = $1 " +
                        "UNION " +
                        "SELECT * " +
                        "FROM related_products rp2 JOIN product p2 on p2.pid = rp2.r_pid " +
                        "WHERE to_pid = $1")
                .execute(Tuple.of(r_pid))
                .compose(records -> {
                    List<Product> productsList = new ArrayList<>();
                    try {
                        records.forEach(row -> {
                            UUID pid = row.getUUID("pid");
                            String name = row.getString("name");
                            String description = row.getString("description");
                            Double price = row.getDouble("price");
                            Integer quantity = row.getInteger("quantity");
                            String brand = row.getString("brand");
                            String category = row.getString("category");
                            Product product = new Product(pid, name, description, price, quantity, brand, category);
                            productsList.add(product);
                        });
                    } catch (Exception e) {
                        return Future.succeededFuture(new ArrayList<>());
                    }
                    return Future.succeededFuture(productsList);
                });
    }


}
