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

    Future<Void> addToCart(User user, UUID id, int quantity);
    Future<Boolean> findInCart(User user, UUID id);

    Future<String> cart(String username);

    Future<Void> checkQuantity(UUID id, int quantity);

    Future<Void> buy(String username);

    Future<Void> logoutUser();

    Future<User> checkLoggedIn();

    Future<Void> removeFromCart(User user, UUID productId, int quantity);
}
