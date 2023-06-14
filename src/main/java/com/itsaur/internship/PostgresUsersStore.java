package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.NoSuchElementException;

public class PostgresUsersStore implements UsersStore {
    Vertx vertx = Vertx.vertx();
    PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("postgres")
            .setUser("postgres")
            .setPassword("password");

    PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String insertQuery = "INSERT INTO users VALUES ($1, $2);";
        return client
                .preparedQuery(insertQuery)
                .execute(Tuple.of(user.username(), user.password()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<User> findUser(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String getQuery = "SELECT * FROM users WHERE username = $1";
        return client
                .preparedQuery(getQuery)
                .execute(Tuple.of(username))
                .compose(rows -> {
                    try {
                        User user = new User(rows.iterator().next().getString(0), rows.iterator().next().getString(1));
                        client.close();
                        return Future.succeededFuture(user);
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<Void> deleteUser(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String insertQuery = "DELETE FROM users WHERE username = $1;";
        return client
                .preparedQuery(insertQuery)
                .execute(Tuple.of(user.username()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String insertQuery = "UPDATE users SET password = $2 WHERE username = $1;";
        return client
                .preparedQuery(insertQuery)
                .execute(Tuple.of(username, password))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }
}
