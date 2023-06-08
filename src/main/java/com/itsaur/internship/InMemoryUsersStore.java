package com.itsaur.internship;

import io.vertx.core.Future;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUsersStore implements UsersStore {
    private Map<String, User> users = new HashMap<>();

    @Override
    public Future<Void> insert(User user) {
        users.put(user.username(), user);
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
        users.remove("username", user.username());
        users.remove("password", user.password());
        return Future.succeededFuture();
    }

}