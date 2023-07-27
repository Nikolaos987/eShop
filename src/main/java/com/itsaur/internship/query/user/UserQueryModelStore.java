package com.itsaur.internship.query.user;

import io.vertx.core.Future;

import java.util.UUID;

public interface UserQueryModelStore {
    Future<UserQueryModel> findUserById(UUID uid);
}
