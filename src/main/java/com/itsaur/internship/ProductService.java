package com.itsaur.internship;

import io.vertx.core.Future;

public class ProductService {
    private final ProductsStore store;

    public ProductService(ProductsStore store) {
        this.store = store;
    }

    public Future<Product> search(String name) {
        return store.findProduct(name)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product == null) {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    } else {
                        System.out.println(product.productId() + "\n" + product.name() + "\n" + product.description() + "\n" + product.brand() + "\n" + product.price() + "\n" + product.quantity() + "\n" + product.category());
                        return Future.succeededFuture(product);
                    }
                });
    }

    public Future<Void> filterProducts(double price, String category) {
        return store.filter(price, category)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products.isEmpty()) {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    } else {
                        products.forEach(product -> {
                            System.out.println("ID:\t\t\t " + product.productId() +
                                    "\nname:\t\t " + product.name() +
                                    "\ndescription: " + product.description() +
                                    "\nbrand:\t\t " + product.brand() +
                                    "\nprice:\t\t " + product.price() +
                                    "\nquantity:\t " + product.quantity() +
                                    "\ncategory:\t " + product.category());
                            System.out.println();
                        });
                        return Future.succeededFuture();
                    }
                });
    }

    public Future<Void> cart(String name, int quantity) {
        return store.findProduct(name)
                .compose(product -> {
                    store.removeQuantity(product.productId(), quantity);
                    return store.addToCart(product.productId(), quantity);
                });

    }
}
