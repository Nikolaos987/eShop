package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.NoSuchElementException;
import java.util.UUID;

public class PostgresUsersStore implements UsersStore {
    Vertx vertx = Vertx.vertx();
    private final PgConnectOptions connectOptions;
    private final PoolOptions poolOptions;

    public PostgresUsersStore(int port, String host, String db, String user, String password, int poolSize) {
        this.connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(db)
                .setUser(user)
                .setPassword(password);
        this.poolOptions = new PoolOptions()
                .setMaxSize(poolSize);
    }

    @Override
    public Future<Void> insert(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String insertQuery = "INSERT INTO customer VALUES ($1, $2, $3);";
        return client
                .preparedQuery(insertQuery)
                .execute(Tuple.of(UUID.randomUUID(), user.username(), user.password()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<User> findUser(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String findQuery = "SELECT * FROM customer WHERE username = $1";
        return client
                .preparedQuery(findQuery)
                .execute(Tuple.of(username))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getString("username"), row.getString("password"));
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
        String deleteQuery = "DELETE FROM customer WHERE username = $1;";
        return client
                .preparedQuery(deleteQuery)
                .execute(Tuple.of(user.username()))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        String updateQuery = "UPDATE customer SET password = $2 WHERE username = $1;";
        return client
                .preparedQuery(updateQuery)
                .execute(Tuple.of(username, password))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }
}
