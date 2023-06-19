package com.itsaur.internship;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.vertx.core.Vertx;

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
                    if (opts.table != null) {
                        /* Server stores to Postgres */
                        vertx.deployVerticle(new ProductApp(
                                new ProductService(new PostgresProductStore(opts.port, opts.host, opts.database, opts.user, opts.password, opts.poolSize))));
                    } else
                    if (opts.file != null) {
                        /* Server stores to File */
                        vertx.deployVerticle(new CustomerApp(
                                new UserService(new UsersStoreBinary(opts.file, "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/temp.txt"))));
                    } else if (opts.database != null) {
                        /* Server stores to Postgres */
                        vertx.deployVerticle(new CustomerApp(
                                new UserService(new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.password, opts.poolSize))));
                    }
                }
                case "console" -> {
                    if (opts.database != null) {
                        /* Console stores to Postgres */
                        new UsersConsole(new UserService(
                                new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize))
                        )
                                .executeCommand(opts) // args
                                .onComplete(v -> System.exit(0));
                    } else if (opts.file != null) {
                        /* Console stores to File */
                        new UsersConsole(new UserService(
                                new UsersStoreBinary(opts.file, "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/temp.txt"))
                        )
                                .executeCommand(opts) // args
                                .onComplete(v -> System.exit(0));
                    } else {
                        /* Console stores to Memory */
                        new UsersConsole(new UserService(
                                new InMemoryUsersStore())
                        )
                                .executeCommand(opts) // args
                                .onComplete(v -> System.exit(0));
                    }
                }
            }
        }  catch (ParameterException e) {
            e.printStackTrace();
            jc.usage();
        }

    }
}