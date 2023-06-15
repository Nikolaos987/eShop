package com.itsaur.internship;

import io.vertx.core.Future;

public class UsersConsole {

    private final UserService userService;

    public UsersConsole(UserService userService) {
        this.userService = userService;
    }

    public Future<Void> executeCommand(Options options) {
        return switch (options.operation) {
            case "register" -> this.userService.register(options.username, options.password)
                    .onSuccess(v -> System.out.println("User registered!"))
                    .onFailure(v -> {
                        System.out.println("Failed to register user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "login" -> this.userService.login(options.username, options.password)
                    .onSuccess(v -> System.out.println("User logged in!"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "delete" -> this.userService.delete(options.username)
                    .onSuccess(v -> System.out.println("User deleted!"))
                    .onFailure(v -> {
                        System.out.println("Failed to delete user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "update" -> this.userService.update(options.username, options.password, options.newPassword)
                    .onSuccess(v -> System.out.println("User updated!"))
                    .onFailure(v -> {
                        System.out.println("Failed to update user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            default -> Future.failedFuture(new IllegalArgumentException("Invalid argument"));
        };
    }
}
