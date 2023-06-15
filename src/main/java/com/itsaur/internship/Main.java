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
                    if (opts.file != null) {
                        /* Server saves to File */
                        vertx.deployVerticle(new App(
                                new UserService(new UsersStoreBinary(opts.file, "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/temp.txt"))));
                    } else if (opts.database != null) {
                        /* Server saves to Postgres */
                        vertx.deployVerticle(new App(
                                new UserService(new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.password, opts.poolSize))));
                    }
                }
                case "console" -> {
                    if (opts.database != null) {
                        /* Console saves to Postgres */
                        new UsersConsole(new UserService(
                                new PostgresUsersStore(opts.port, opts.host, opts.database, opts.user, opts.postPasword, opts.poolSize))
                        )
                                .executeCommand(opts) // args
                                .onComplete(v -> System.exit(0));
                    } else {
                        /* Console saves to Memory */
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