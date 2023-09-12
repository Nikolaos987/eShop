package com.itsaur.internship.userEntity;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.nio.file.FileSystem;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostgresUsersStore implements UsersStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public PostgresUsersStore(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Future<UUID> insert(User user) {
        return pgPool
                .preparedQuery("INSERT INTO users VALUES ($1, $2, $3);")
                .execute(Tuple.of(user.uid(), user.username(), user.password()))
                .compose(v -> Future.succeededFuture(user.uid()));
    }

    @Override
    public Future<User> findUser(String username) {
        return pgPool
                .preparedQuery("SELECT * FROM users WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(
                                row.getUUID("uid"),
                                row.getString("username"),
                                row.getString("password"));
                        return Future.succeededFuture(user);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<User> findUserById(UUID uid) {
        return pgPool
                .preparedQuery("SELECT * FROM users WHERE uid = $1")
                .execute(Tuple.of(uid))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(
                                row.getUUID("uid"),
                                row.getString("username"),
                                row.getString("password"));
                        return Future.succeededFuture(user);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<User> findUser(UUID userId) {
        return pgPool
                .preparedQuery("SELECT * FROM users WHERE uid = $1")
                .execute(Tuple.of(userId))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getUUID("uid"), row.getString("username"), row.getString("password"));
                        return Future.succeededFuture(user);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<Void> deleteUser(UUID uid) {
        return pgPool
                .preparedQuery("DELETE FROM users WHERE uid = $1;")
                .execute(Tuple.of(uid))
                .compose(v2 -> Future.succeededFuture());
    }

    @Override
    public Future<UUID> updateUser(String username, String password) {
        return pgPool
                .preparedQuery("UPDATE users SET password = $2 WHERE username = $1;")
                .execute(Tuple.of(username, password))
                .compose(v -> pgPool
                        .preparedQuery("SELECT uid FROM users WHERE username = $1")
                        .execute(Tuple.of(username))
                        .compose(records -> Future.succeededFuture(records.iterator().next().getUUID("uid"))));
    }

}
