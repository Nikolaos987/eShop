package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class CartService {

    private final CartsStore store;

    public CartService(CartsStore store) {
        this.store = store;
    }


    public Future<Void> addCart(UUID userID, UUID productId, int quantity) {
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

    public Future<Void> removeCart(UUID cartId, UUID productId, int quantity) {
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

    public Future<JsonArray> showCart(UUID cid) {
        return store.cart(cid);
    }

    public Future<Void> buyCart(UUID cid) {
        return store.buy(cid);
    }

    public Future<JsonObject> getProduct(User user, UUID productId) {
        if (user != null)
            return store.getProduct(productId);
        return Future.failedFuture(new IllegalArgumentException("you are not logged-in!"));
    }

}
