package com.itsaur.internship;

import io.vertx.core.Future;

public interface UsersStore {
    Future<Void> insert(User user);

    Future<User> findUser(String username);

    Future<Void> deleteUser(User user);
}
