package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class ProductService {

    private final ProductsStore store;

    public ProductService(ProductsStore store) {
        this.store = store;
    }


    public Future<JsonObject> product(UUID productId) {
        return store.getProduct(productId)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null) {
                        return Future.succeededFuture(product);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<JsonArray> searchByName(String name) {
        return store.findProducts(name)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products.size() != 0) {
                        return Future.succeededFuture(products);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<JsonArray> filterProducts(double price, String brand, String category) {
        return store.filter(price, brand , category)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products != null) {
                        return Future.succeededFuture(products);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("no products found"));
                    }
                });
    }
}
