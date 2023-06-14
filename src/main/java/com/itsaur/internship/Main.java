package com.itsaur.internship;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

//        final UserService service = new UserService(new UsersStoreBinary());

        switch (args[0]) {
            case "--server" -> vertx.deployVerticle(new App(
                    new UserService(new UsersStoreBinary("/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/bin.txt",
                            "/home/souloukos@ad.itsaur.com/IdeaProjects/EshopAPI/src/main/resources/temp.txt")))
            );
            case "--console" -> new UsersConsole(new UserService(
                    new InMemoryUsersStore())
            )
                    .executeCommand(args)
                    .onComplete(v -> System.exit(0));
            case "--postgres" -> vertx.deployVerticle(new App(
                    new UserService(new PostgresUsersStore(5432, "localhost", "postgres", "postgres", "password", 5))
            ));
        }
    }
}