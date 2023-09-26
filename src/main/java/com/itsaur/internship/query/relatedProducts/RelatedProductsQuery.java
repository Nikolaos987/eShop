package com.itsaur.internship.query.relatedProducts;

import com.beust.ah.A;
import com.itsaur.internship.productEntity.Product;
import com.itsaur.internship.query.product.ProductsQueryModel;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RelatedProductsQuery implements RelatedProductsQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public RelatedProductsQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }


    @Override
    public Future<RelatedProductsQueryModel> getRelatedProducts(UUID r_pid) {
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
                    RelatedProductsQueryModel relatedProductsQueryModel;
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
                        relatedProductsQueryModel = new RelatedProductsQueryModel(productsList);
                    } catch (Exception e) {
                        return Future.succeededFuture(new RelatedProductsQueryModel(new ArrayList<>()));
                    }
                    return Future.succeededFuture(relatedProductsQueryModel);
                });
    }
}
