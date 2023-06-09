package com.itsaur.internship;

import io.vertx.core.Future;

public class UsersConsole {

    private final UserService userService;

    public UsersConsole(UserService userService) {
        this.userService = userService;
    }

    public Future<Void> executeCommand(String[] args) {
        return switch (args[1]) {
            case "--register" -> this.userService.register(args[2], args[3])
                    .onSuccess(v -> System.out.println("User registered!"))
                    .onFailure(v -> {
                        System.out.println("Failed to register user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "--login" -> this.userService.login(args[2], args[3])
                    .onSuccess(v -> System.out.println("User logged in!"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "--delete" -> this.userService.delete(args[2])
                    .onSuccess(v -> System.out.println("User deleted!"))
                    .onFailure(v -> {
                        System.out.println("Failed to delete user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "--update" -> this.userService.update(args[2], args[3], args[4])
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
