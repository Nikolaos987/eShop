package com.itsaur.internship;

import io.vertx.core.Future;

public class UserService {

    private UsersStore store;

    public UserService(UsersStore store) {
        this.store = store;
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

    public Future<Void> delete(String username) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
                    } else {
                        return store.deleteUser(user);
                    }
                });
    }

    public Future<Void> update(String username, String currentPassword, String newPassword) {
        return store.findUser(username)
                .compose(user -> {
                    if (user.matches(currentPassword)) {
                        return store.deleteUser(user)
                                .compose(newUser -> store.insert(new User(username, newPassword)));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("passwords do not match"));
                    }
                });
    }

}