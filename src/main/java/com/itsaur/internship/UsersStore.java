package com.itsaur.internship;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface UsersStore {
    Future<Void> insert(User user);

    Future<User> findUser(String username);

    Future<Void> deleteUser(User user);

    Future<Void> updateUser(String username, String password);

        /* for products interaction */

    Future<Product> findProduct(String name);

    Future<ArrayList<Product>> filter(double price, String category);

    Future<Void> addToCart(UUID id, int quantity);

    Future<Void> checkQuantity(UUID id, int quantity);
}
