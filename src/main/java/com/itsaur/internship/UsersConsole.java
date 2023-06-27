package com.itsaur.internship;

import io.vertx.core.Future;

public class UsersConsole {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public UsersConsole(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    public Future<Void> executeCommand(Options options) {
        return switch (options.operation) {
            case "login" -> this.userService.login(options.username, options.password)
                    .onSuccess(v -> System.out.println("User logged-in"))
                    .onFailure(v -> {
                        System.out.println("Failed to log in user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "register" -> this.userService.register(options.username, options.password)
                    .onSuccess(v -> System.out.println("User registered!"))
                    .onFailure(v -> {
                        System.out.println("Failed to register user");
                        v.printStackTrace();
                    })
                    .mapEmpty();
//            case "delete" -> this.userService.delete(options.username)
//                    .onSuccess(v -> System.out.println("User deleted!"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to delete user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "update" -> this.userService.update(options.username, options.password, options.newPassword)
//                    .onSuccess(v -> System.out.println("User updated!"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to update user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();


            // TODO: 26/6/23 find product implementation
            case "search" -> this.productService.product(options.productId)
                    .onSuccess(System.out::println)
                    .onFailure(v -> {
                        System.out.println("product does not exist");
                        v.printStackTrace();
                    })
                    .mapEmpty();
            case "filter" -> this.productService.filterProducts(options.price, options.brand, options.category)
                    .onSuccess(System.out::println)
                    .onFailure(v -> {
                        System.out.println("products not found");
                        v.printStackTrace();
                    })
                    .mapEmpty();




//            case "cart" -> this.cartService.addItem(options.productId, options.quantity)
//                    .onSuccess(v -> System.out.println("products added to cart"))
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "remove" -> this.cartService.removeItem(options.productId, options.quantity)
//                    .onSuccess(v -> System.out.println("products quantity removed from cart"))
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "myCart" -> this.cartService.showCart()
//                    .onSuccess(System.out::println)
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "buy" -> this.cartService.buyCart()
//                    .onSuccess(v -> System.out.println("bought products in your cart successfully!"))
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
            default -> Future.failedFuture(new IllegalArgumentException("Invalid argument"));
        };
    }
}
