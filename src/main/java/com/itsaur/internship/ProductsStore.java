package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public interface ProductsStore {

    Future<JsonObject> getProduct(UUID productId);

    Future<JsonArray> findProducts(String name);

    Future<JsonArray> filter(double price, String brand, String category);

    Future<Void> create(String name, String description, double price, int quantity, String brand, String category);

}
