package com.itsaur.internship;

import io.vertx.core.Future;

public class UsersConsole {

    private final UserService userService;

    public UsersConsole(UserService userService) {
        this.userService = userService;
    }

    public Future<Void> executeCommand(String[] args) {
        if (args[1].equals("--register")) {
            return this.userService.register(args[2], args[3])
                    .onSuccess(v -> System.out.println("User registered!"))
                    .onFailure(v -> {
                        System.out.println("Failed to register user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
        } else if (args[1].equals("--login")) {
            return this.userService.login(args[2], args[3])
                    .onSuccess(v -> System.out.println("User logged in!"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
        } else {
            return Future.failedFuture(new IllegalArgumentException("Invalid argument"));
        }
    }
}
