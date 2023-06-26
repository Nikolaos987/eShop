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

}
