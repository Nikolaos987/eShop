package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class UserService {

    private final UsersStore store;

    public UserService(UsersStore store) {
        this.store = store;
    }

    public Future<User> login(String username, String password) {
        return store.findUser(username)
                .compose(user -> {
                    if (user.matches(password)) {
                        return Future.succeededFuture(user);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("Invalid password"));
                    }
                });
    }

    public Future<Void> register(String username, String password) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        return store.insert(new User(username, password));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User already exists"));
                    }
                });
    }

    public Future<Void> delete(String username) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null) {
                        return store.deleteUser(user);  // .compose(v2 -> Future.succeededFuture());
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
                    }
                });
    }

    public Future<Void> update(String username, String currentPassword, String newPassword) {
        return store.findUser(username)
                .compose(user -> {
                    if (user.matches(currentPassword)) {
                        return store.updateUser(username, newPassword);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("passwords do not match"));
                    }
                });
    }









}