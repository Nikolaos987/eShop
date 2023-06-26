package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

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
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(u -> {
                    if (u != null)
                        return store.logoutUser();
                    else
                        return Future.failedFuture(new IllegalArgumentException("user already logged out"));
                });
    }

    public Future<Void> delete(String username) {
        return store.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null) {
                        return store.logoutUser()
                                .onSuccess(v -> store.deleteUser(user)
                                        .compose(v2 -> Future.succeededFuture()));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
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

    public Future<JsonObject> product(UUID productId) {
        return store.getProduct(productId)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null) {
                        return Future.succeededFuture(product);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<JsonArray> searchByName(String name) {
        return store.findProducts(name)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products.size() != 0) {
                        return Future.succeededFuture(products);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<JsonArray> filterProducts(double price, String brand, String category) {
        return store.filter(price, brand , category)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products != null) {
                        return Future.succeededFuture(products);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("no products found"));
                    }
                });
    }

    public Future<Void> addCart(UUID productId, int quantity) {
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(user -> getProduct(user, productId)
                        .compose(Future::succeededFuture))
                .compose(product -> {
                    if (product != null)
                        return store.checkQuantity((UUID) product.getValue("PRODUCT ID"), quantity)
                                // TODO: 22/6/23 if quantity >= 0 then...
                                .compose(v -> store.addToCart(User.getUser(), (UUID) product.getValue("PRODUCT ID"), quantity));
                    return Future.failedFuture(new IllegalArgumentException("product was not found"));
                });
    }

    public Future<Void> removeCart(UUID productId, int quantity) {
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(user -> getProduct(user, productId)
                        .compose(Future::succeededFuture))
                .compose(product -> {
                    if (product != null) {
                        User user = User.getUser();
                        return store.findInCart(user, (UUID) product.getValue("PRODUCT ID"))
                                .compose(exists -> {
                                    if (exists) {
                                        return store.removeFromCart(user, (UUID) product.getValue("PRODUCT ID"), quantity)
                                                .compose(v -> Future.succeededFuture());
                                    }
                                    return Future.failedFuture(new IllegalArgumentException("product does not exist in your cart"));
                                });
                    }
                    return Future.failedFuture(new IllegalArgumentException("product does not exist"));
                });
    }

    public Future<JsonArray> showCart() {
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return store.cart(user.username());
                    return Future.failedFuture(new IllegalArgumentException("you are not logged-in!"));
                });
    }

    public Future<Void> buyCart() {
        return store.checkLoggedIn()
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return store.buy(user.username());
                    return Future.failedFuture(new IllegalArgumentException("you are not logged-in!"));
                });
    }

    public Future<JsonObject> getProduct(User user, UUID productId) {
        if (user != null)
            return store.getProduct(productId);
        return Future.failedFuture(new IllegalArgumentException("you are not logged-in!"));
    }

}