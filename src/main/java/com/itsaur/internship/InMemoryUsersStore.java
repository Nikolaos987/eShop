package com.itsaur.internship;

import io.vertx.core.Future;

import java.util.HashMap;
import java.util.Map;

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
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        users.replace(username, new User(username, password));
        return Future.succeededFuture();
    }

}