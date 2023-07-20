package com.itsaur.internship;

import cartEntity.CartService;
import cartEntity.PostgresCartsStore;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.vertx.core.Vertx;
import productEntity.PostgresProductsStore;
import productEntity.ProductService;
import query.cart.CartQuery;
import query.product.ProductQuery;
import query.user.UserQuery;
import userEntity.PostgresUsersStore;
import userEntity.UserService;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        var opts = new Options();
        var jc = JCommander.newBuilder()
                .addObject(opts)
                .programName("jcommander")
                .build();

        try {
            jc.parse(args);
            switch (opts.method) {
                case "server" -> {
                    if (opts.database != null) {
                        /* Server stores to Postgres */
                        vertx.deployVerticle(new Api(
                                new UserService(
                                        new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                        new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize)),
                                new ProductService(
                                        new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                        new CartService(
                                                new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                                new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                                new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize))),
                                new CartService(
                                        new PostgresCartsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                        new PostgresProductsStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                        new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize)),
                                new CartQuery(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                new ProductQuery(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize),
                                new UserQuery(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize)));
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