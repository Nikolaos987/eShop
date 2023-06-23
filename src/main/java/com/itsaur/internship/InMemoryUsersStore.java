package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryUsersStore implements UsersStore {
    private final Map<String, User> users = new HashMap<>()
    {{
        put("One", new User("One", "1"));
        put("Two", new User("Two", "12"));
        put("Three", new User("Three", "123"));
        put("Four", new User("Four", "1234"));
        put("Five", new User("Five", "12345"));
    }};


    @Override
    public Future<Void> insert(User user) {
        users.put(user.username(), user);
        printUsers(users);
        return Future.succeededFuture();
    }

    @Override
    public Future<User> findUser(String username) {
        User user = users.get(username);
        if (user == null) {
            return Future.failedFuture(new IllegalArgumentException("User not found"));
        } else {
            return Future.succeededFuture(user);
        }
    }

    @Override
    public Future<Void> deleteUser(User user) {
        users.remove(user.username());
        printUsers(users);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        users.replace(username, new User(username, password));
        printUsers(users);
        return Future.succeededFuture();
    }

    @Override
    public Future<Product> findProduct(String name) {
        return null;
    }

    @Override
    public Future<ArrayList<Product>> filter(double price, String category) {
        return null;
    }

    @Override
    public Future<Void> addToCart(User user, UUID id, int quantity) {
        return null;
    }

    @Override
    public Future<Boolean> findInCart(User user, UUID id) {
        return null;
    }

    @Override
    public Future<JsonArray> cart(String username) {
        return null;
    }

    @Override
    public Future<Void> checkQuantity(UUID id, int quantity) {
        return null;
    }

    public Future<Void> buy(String username) {
        return null;
    }

    @Override
    public Future<Void> logoutUser() {
        return null;
    }

    @Override
    public Future<User> checkLoggedIn() {
        return null;
    }

    @Override
    public Future<Void> removeFromCart(User user, UUID productId, int quantity) {
        return null;
    }

    public void printUsers(Map<String, User> users) {
        users.forEach((u,p) -> System.out.println(p));
    }

}