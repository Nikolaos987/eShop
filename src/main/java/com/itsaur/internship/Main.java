package com.itsaur.internship;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

//        final UserService service = new UserService(new UsersStoreBinary());

        if (args[0].equals("--server")) {
            vertx.deployVerticle(new MyVerticle(
                    new UserService(new UsersStoreBinary()))
            );
        } else if (args[0].equals("--console")) {
            new UsersConsole(new UserService(
                    new InMemoryUsersStore())
            ).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        }
    }
}