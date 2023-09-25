package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartService;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.UUID;

public class ProductService {

    private final ProductsStore productsStore;
    private final CartService cartService;

    public ProductService(ProductsStore productsStore, CartService cartService) {
        this.productsStore = productsStore;
        this.cartService = cartService;
    }


    public Future<Product> addProduct(String name, String description,
                                   double price, int quantity, String brand, String category) {
        return productsStore.findProduct(name)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product == null)
                        return productsStore.insert(
                                new Product(UUID.randomUUID(),
                                name, description, price, quantity, brand, category))
                                .compose(newProduct -> Future.succeededFuture(newProduct));
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

    public Future<Void> updateProduct(UUID pid, String name, String image, String description, double price, int quantity, String brand, String category) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return productsStore.updateProduct(new Product(pid, name, description, price, quantity, brand, category));
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> insertImage(UUID pid, Buffer buffer) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null && buffer.getBytes().length > 0)
                        return productsStore.insertImage(pid, buffer);
                    return Future.failedFuture(new IllegalArgumentException("image could not be read"));
                });
    }

    public Future<UUID> relateProduct(UUID r_pid, UUID to_pid) {
        return productsStore.findRelatedProduct(r_pid, to_pid)
                .otherwiseEmpty()
                .compose(pid -> {
                    if (pid == null) {
                        return productsStore.addRelatedProduct(r_pid, to_pid);
                    }
                    return Future.failedFuture(new IllegalArgumentException("relation already exists"));
                });
    }

}
