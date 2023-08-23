package com.itsaur.internship;

import com.itsaur.internship.cartEntity.CartService;
import com.itsaur.internship.cartEntity.PostgresCartsStore;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.vertx.core.Vertx;
import com.itsaur.internship.productEntity.PostgresProductsStore;
import com.itsaur.internship.productEntity.ProductService;
import com.itsaur.internship.query.cart.CartQuery;
import com.itsaur.internship.query.product.ProductQuery;
import com.itsaur.internship.query.user.UserQuery;
import com.itsaur.internship.userEntity.PostgresUsersStore;
import com.itsaur.internship.userEntity.UserService;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        var opts = new Options();
        var jc = JCommander.newBuilder()
                .addObject(opts)
                .programName("jcommander")
                .build();

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(opts.port)
                .setHost(opts.host)
                .setDatabase(opts.database)
                .setUser(opts.user)
                .setPassword(opts.password);

        PoolOptions poolOptions = new PoolOptions().setMaxSize(opts.poolSize);


        PgPool pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

        try {
            jc.parse(args);
            switch (opts.method) {
                case "server" -> {
                    if (opts.database != null) {
                        /* Server stores to Postgres */
                        vertx.deployVerticle(new Api(
                                new UserService(
                                        new PostgresUsersStore(pgPool),
                                        new PostgresCartsStore(pgPool)),
                                new ProductService(
                                        new PostgresProductsStore(pgPool),
                                        new CartService(
                                                new PostgresCartsStore(pgPool),
                                                new PostgresProductsStore(pgPool),
                                                new PostgresUsersStore(pgPool))),
                                new CartService(
                                        new PostgresCartsStore(pgPool),
                                        new PostgresProductsStore(pgPool),
                                        new PostgresUsersStore(pgPool)),
                                new CartQuery(pgPool),
                                new ProductQuery(pgPool),
                                new UserQuery(pgPool)));
                    }
                }
//                case "console" -> {
//                    if (opts.database != null) {
//                        /* Console stores to Postgres */
//                        new Console(
//                                new UserService(
//                                        new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                        new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize)),
//                                new ProductService(
//                                        new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                        new CartService(
//                                                new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                                new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                                new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize))),
//                                new CartService(
//                                        new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                        new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
//                                        new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize)))
//                                .executeCommand(opts) // args
//                                .onComplete(v -> System.exit(0));
//                    }
//                }
            }
        }  catch (ParameterException e) {
            e.printStackTrace();
            jc.usage();
        }

    }
}