package com.itsaur.internship;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        final UserService service = new UserService(new UsersStoreBinary()); // new UsersStoreBinary() // new InMemoryUsersStore

        if (args[0].equals("--server")) {
            vertx.deployVerticle(new LoginVerticle(service));
        } else if (args[0].equals("--console")) {
            new UsersConsole(service).executeCommand(args)
                    .onComplete(v -> System.exit(0));
        }

//        LoginVerticle loginVerticle = new LoginVerticle();
//        vertx.deployVerticle(loginVerticle);

    }
}