package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public interface UsersStore {
    Future<Void> insert(User user);

    Future<User> findUser(String username);

    Future<Void> deleteUser(User user);

    Future<Void> updateUser(String username, String password);

        /* for products interaction */

    Future<JsonObject> findProduct(String name);

    Future<JsonArray> filter(double price, String category);

    Future<Void> addToCart(User user, UUID id, int quantity);

    Future<Boolean> findInCart(User user, UUID id);

    Future<JsonArray> cart(String username);

    Future<Void> checkQuantity(UUID id, int quantity);

    Future<Void> buy(String username);

    Future<Void> logoutUser();

    Future<User> checkLoggedIn();

    Future<Void> removeFromCart(User user, UUID productId, int quantity);
}
