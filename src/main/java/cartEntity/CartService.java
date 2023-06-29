package cartEntity;

import io.vertx.core.Future;
import productEntity.ProductsStore;

import java.util.Collection;
import java.util.UUID;

public class CartService {

    private final CartsStore cartsStore;
    private final ProductsStore productsStore;

    public CartService(CartsStore cartsStore, ProductsStore productsStore) {
        this.cartsStore = cartsStore;
        this.productsStore = productsStore;
    }

    public Future<CartItem> showCartItem(UUID itemid) {
        return cartsStore.findCartItem(itemid);
    }

    public Future<Collection<CartItem>> showCartItems(UUID uid) {
        return cartsStore.findCartItems(uid);
    }

    public Future<Void> addItem(UUID uid, UUID pid, int quantity) {
        return cartsStore.findCartItem(uid, pid)
                .otherwiseEmpty()
                .compose(item -> {
                    if (item == null) {
                        return cartsStore.findCart(uid)
                                .compose(cart -> cartsStore.insert(new CartItem(UUID.randomUUID(), pid, quantity), cart.cid()));
                    }
                    return cartsStore.updateCartItem(item, quantity);
                });
    }

    public Future<Void> buyCart(UUID userId) {
        return cartsStore.findCartItems(userId)
                .compose(collectionOfCartItems -> {
                    collectionOfCartItems.forEach(cartItem -> {
                        cartsStore.removeCartItem(cartItem);
                        productsStore.updateProduct(cartItem.pid(), cartItem.quantity());
                    });
                    return Future.succeededFuture();
                });
    }

}
