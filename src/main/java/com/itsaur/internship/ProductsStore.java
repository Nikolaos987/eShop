package com.itsaur.internship;

import io.vertx.core.Future;
import java.util.ArrayList;
import java.util.UUID;

public interface ProductsStore {

    Future<Product> findProduct(String name);

    Future<ArrayList<Product>> filter(double price, String category);

    Future<Void> addToCart(UUID id, int quantity);

    Future<Void> removeQuantity(UUID id, int quantity);

}
