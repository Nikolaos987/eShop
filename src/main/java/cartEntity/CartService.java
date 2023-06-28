package cartEntity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.UUID;

public class CartService {

    private final CartsStore store;

    public CartService(CartsStore store) {
        this.store = store;
    }

    public Future<JsonArray> showCart(UUID userId) {
        return store.cart(userId);
    }

    public Future<Void> addItem(UUID userId, UUID productId, int quantity) {
        return store.checkQuantity(productId, quantity)
                // TODO: 22/6/23 if quantity >= 0 then...
                .compose(v -> store.addToCart(userId, productId, quantity));
    }

    public Future<Void> buyCart(UUID userId) {
        return store.buy(userId);
    }

    public Future<Void> removeItem(UUID userId, UUID productId, int quantity) {
        return store.findItem(userId, productId) // TODO: 27/6/23 return void instead of boolean
                .compose(exists -> {
                    if (exists) {
                        return store.removeFromCart(userId, productId, quantity)
                                .compose(v -> Future.succeededFuture());
                    }
                    return Future.failedFuture(new IllegalArgumentException("product does not exist in your cart"));
                });
    }

}
