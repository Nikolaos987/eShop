package com.itsaur.internship.query.user;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import com.itsaur.internship.userEntity.User;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserQuery implements UserQueryModelStore {
    Vertx vertx = Vertx.vertx();
    private final PgPool pgPool;

    public UserQuery(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Future<UserQueryModel> findUserById(UUID uid) {
        return pgPool
                .preparedQuery("SELECT * FROM users WHERE uid = $1")
                .execute(Tuple.of(uid))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getUUID("uid"), row.getString("username"), row.getString("password"));
                        return Future.succeededFuture(new UserQueryModel(user.username()));
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }
}
