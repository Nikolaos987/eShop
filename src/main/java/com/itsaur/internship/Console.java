package com.itsaur.internship;

import cartEntity.CartService;
import io.vertx.core.Future;
import productEntity.Category;
import productEntity.ProductService;
import userEntity.UserService;

import java.util.UUID;

public class Console {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    public Console(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

//    public Future<Void> executeCommand(Options options) {
//        return switch (options.operation) {
//            case "login" -> this.userService.login(options.username, options.password)
//                    .onSuccess(v -> System.out.println("User logged-in"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to log in user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "register" -> this.userService.register(options.username, options.password)
//                    .onSuccess(v -> System.out.println("User registered!"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to register user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "delete" -> this.userService.delete(UUID.fromString(options.uid))
//                    .onSuccess(v -> System.out.println("User deleted!"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to delete user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "update" -> this.userService.update(UUID.fromString(options.uid), options.password, options.newPassword)
//                    .onSuccess(v -> System.out.println("User updated!"))
//                    .onFailure(v -> {
//                        System.out.println("Failed to update user");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//
//
//
//
//            case "insert" -> this.productService.addProduct(options.name, options.imagePath, options.desc, options.price, options.quantity, options.brand, Category.valueOf(options.category))
//                    .onSuccess(v -> System.out.println("product added!"))
//                    .onFailure(v -> {
//                        System.out.println("product does not exist");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "delete-product" -> this.productService.deleteProduct(UUID.fromString(options.pid))
//                    .onSuccess(v -> System.out.println("product deleted!"))
//                    .onFailure(v -> {
//                        System.out.println("product does not exist");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "update-product" -> this.productService.updateProductPrice(UUID.fromString(options.pid), options.price)
//                    .onSuccess(System.out::println)
//                    .onFailure(v -> {
//                        System.out.println("product does not exist");
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//
//
//
//
//            case "item-update" -> this.cartService.addItem(UUID.fromString(options.uid), UUID.fromString(options.pid), options.quantity)
//                    .onSuccess(v -> System.out.println("products added to cart"))
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            case "buy" -> this.cartService.buyCart(UUID.fromString(options.uid))
//                    .onSuccess(v -> System.out.println("bought products in your cart successfully!"))
//                    .onFailure(v -> {
//                        System.out.println(v.getMessage());
//                        v.printStackTrace();
//                    })
//                    .mapEmpty();
//            default -> Future.failedFuture(new IllegalArgumentException("Invalid argument"));
//        };
//    }
}
