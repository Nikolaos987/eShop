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
                    .onSuccess(v -> System.out.println("User logged-in"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "logout" -> this.userService.logout()
                    .onSuccess(v -> System.out.println("you are logged-out successfully"))
                    .onFailure(v -> {
                        System.out.println("Failed to log-out. You are not logged in");
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
            case "search" -> this.userService.search(options.name)
                    .onSuccess(v -> System.out.println("product found"))
                    .onFailure(v -> {
                        System.out.println("product does not exist");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "filter" -> this.userService.filterProducts(options.price, options.brand, options.category)
                    .onSuccess(v -> System.out.println("products found"))
                    .onFailure(v -> {
                        System.out.println("products not found");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "cart" -> this.userService.addCart(options.name, options.quantity)
                    .onSuccess(v -> System.out.println("products added to cart"))
                    .onFailure(v -> {
                        System.out.println(v.getMessage());
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "remove" -> this.userService.removeCart(options.name, options.quantity)
                    .onSuccess(v -> System.out.println("products quantity removed from cart"))
                    .onFailure(v -> {
                        System.out.println(v.getMessage());
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "myCart" -> this.userService.showCart()
                    .onSuccess(System.out::println)
                    .onFailure(v -> {
                        System.out.println(v.getMessage());
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "buy" -> this.userService.buyCart()
                    .onSuccess(v -> System.out.println("bought products in your cart successfully!"))
                    .onFailure(v -> {
                        System.out.println(v.getMessage());
                        v.printStackTrace();
                    })
                    .mapEmpty();
            default -> Future.failedFuture(new IllegalArgumentException("Invalid argument"));
        };
    }
}
