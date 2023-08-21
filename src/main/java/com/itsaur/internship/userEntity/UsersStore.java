package com.itsaur.internship.userEntity;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public interface UsersStore {
    Future<UUID> insert(User user);

    Future<User> findUser(String username);

    Future<User> findUser(UUID uid);

    Future<Void> deleteUser(UUID uid);

    Future<UUID> updateUser(String username, String password);

}
