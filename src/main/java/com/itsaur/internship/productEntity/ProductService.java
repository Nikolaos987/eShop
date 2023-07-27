package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartService;
import io.vertx.core.Future;

import java.util.UUID;

public class ProductService {

    private final ProductsStore productsStore;
    private final CartService cartService;

    public ProductService(ProductsStore productsStore, CartService cartService) {
        this.productsStore = productsStore;
        this.cartService = cartService;
    }

    public Future<Void> addProduct(String name, String imagePath, String description, double price, int quantity, String brand, Category category) {
        System.out.println(imagePath);
        return productsStore.findProduct(name)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product == null)
                        return productsStore.insert(new Product(UUID.randomUUID(), name, imagePath, description, price, quantity, brand, category));
                    return Future.failedFuture(new IllegalArgumentException("product with this name already exists!"));
                });
    }

    public Future<Void> deleteProduct(UUID pid) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return cartService.deleteCartItems(pid)
                                .compose(result -> productsStore.deleteProduct(pid));
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> updateProduct(UUID pid, String name, String image, String description, double price, int quantity, String brand, Category category) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return productsStore.updateProduct(new Product(pid, name, image, description, price, quantity, brand, category));
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

}
