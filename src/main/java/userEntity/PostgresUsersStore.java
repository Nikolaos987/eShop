package userEntity;

import cartEntity.Cart;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDateTime;
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
        // TODO: 3/7/23 insert new cart
        return client
                .preparedQuery("INSERT INTO users VALUES ($1, $2, $3);")
                .execute(Tuple.of(user.uid(), user.username(), user.password()))
                .compose(v -> {
                    Cart cart = new Cart(UUID.randomUUID(), user.uid(), LocalDateTime.now(), null);
                    return client
                            .preparedQuery("INSERT INTO cart VALUES ($1, $2, $3)")
                            .execute(Tuple.of(cart.cid(), cart.uid(), cart.dateCreated()))
                            .compose(v2 -> Future.succeededFuture());
                });
    }

    @Override
    public Future<User> findUser(String username) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM users WHERE username = $1")
                .execute(Tuple.of(username))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getUUID("uid"), row.getString("username"), row.getString("password"));
                        return client.close()
                                .compose(r -> Future.succeededFuture(user));
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<User> findUser(UUID userId) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("SELECT * FROM users WHERE uid = $1")
                .execute(Tuple.of(userId))
                .compose(rows -> {
                    try {
                        Row row = rows.iterator().next();
                        User user = new User(row.getUUID("uid"), row.getString("username"), row.getString("password"));
                        return client.close()
                                .compose(r -> Future.succeededFuture(user));
                    } catch (NoSuchElementException e) {
                        return Future.failedFuture(new IllegalArgumentException("User not found"));
                    }
                });
    }

    @Override
    public Future<Void> deleteUser(User user) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("DELETE FROM cartitem WHERE itemid IN (SELECT itemid FROM cart c JOIN cartitem ci ON c.cid = ci.cid WHERE c.uid = $1);")
                .execute(Tuple.of(user.uid()))
                .compose(v -> client
                        .preparedQuery("DELETE FROM cart WHERE uid = $1")
                        .execute(Tuple.of(user.uid()))
                        .compose(v1 -> client
                                .preparedQuery("DELETE FROM users WHERE username = $1;")
                                .execute(Tuple.of(user.username()))
                                .compose(v2 -> client.close())
                                .compose(v2 -> Future.succeededFuture())));
    }

    @Override
    public Future<Void> updateUser(String username, String password) {
        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        return client
                .preparedQuery("UPDATE users SET password = $2 WHERE username = $1;")
                .execute(Tuple.of(username, password))
                .compose(v -> client.close())
                .compose(v -> Future.succeededFuture());
    }

}
