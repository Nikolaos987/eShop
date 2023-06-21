package com.itsaur.internship;

import io.vertx.core.Future;

public class UserService {

    private final UsersStore store;

    public UserService(UsersStore store) {
        this.store = store;
    }

    public Future<Void> register(String username, String password) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        return store.insert(new User(username, password));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User already exists"));
                    }
                });
    }

    public Future<User> login(String username, String password) {
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(u -> {
                    if (u == null) {
                        return store.findUser(username)
                                .compose(user -> {
                                    if (user.matches(password)) {
                                        user.remember(user);
                                        return Future.succeededFuture(user);
                                    } else {
                                        return Future.failedFuture(new IllegalArgumentException("Invalid password"));
                                    }
                                });
                    }
                    return Future.failedFuture(new IllegalArgumentException("Already logged in"));
                });

    }

    public Future<Void> logout() {
        return store.logoutUser();
    }

    public Future<Void> delete(String username) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
                    } else {
                        return store.logoutUser()
                                .onSuccess(v -> store.deleteUser(user));
                    }
                });
    }

    public Future<Void> update(String username, String currentPassword, String newPassword) {
        return store.findUser(username)
                .compose(user -> {
                    if (user.matches(currentPassword)) {
                        return store.updateUser(username, newPassword);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("passwords do not match"));
                    }
                });
    }

    /* for products interaction */

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
                    if (products == null) {
                        return Future.failedFuture(new IllegalArgumentException("products not found"));
                    } else {
                        return Future.succeededFuture();
                    }
                });
    }

    public Future<Void> cart(String name, int quantity) {
        return store.checkLoggedIn().compose(u ->
                store.findProduct(name)
                        .compose(product -> store.checkQuantity(product.productId(), quantity)
                                .compose(v -> {
                                    String usr = null;
                                    String pass = null;
                                    User user = new User(User.pref.get("username", usr), User.pref.get("password", pass));
                                    return store.addToCart(user, product.productId(), quantity);
                                })));
    }

    public Future<Void> buyCart() {
        return store.buy();
    }

}