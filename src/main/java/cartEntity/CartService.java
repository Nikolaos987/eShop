package cartEntity;

import io.vertx.core.Future;
import productEntity.ProductsStore;

import java.util.Collection;
import java.util.UUID;

public class CartService {

    private final CartsStore store;
    private final ProductsStore storeProduct;

    public CartService(CartsStore store, ProductsStore storeProduct) {
        this.store = store;
        this.storeProduct = storeProduct;
    }

    public Future<CartItem> showCartItem(UUID itemid) {
        return store.findCartItem(itemid);
    }

    public Future<Collection<CartItem>> showCartItems(UUID uid) {
        return store.findCartItems(uid);
    }

    public Future<Void> addItem(UUID uid, UUID pid, int quantity) {
        return store.findCartItem(uid, pid)
                .otherwiseEmpty()
                .compose(item -> {
                    if (item == null) {
                        return store.findCart(uid)
                                .compose(cart -> store.insert(new CartItem(UUID.randomUUID(), pid, quantity), cart.cid()));
                    }
                    return store.updateCartItem(item, quantity);
                });
    }

    public Future<Void> buyCart(UUID userId) {
        return store.findCartItems(userId)
                .compose(collectionOfCartItems -> {
                    collectionOfCartItems.forEach(cartItem -> {
                        store.removeCartItem(cartItem);
                        storeProduct.updateProduct(cartItem.pid(), cartItem.quantity());
                    });
                    return Future.succeededFuture();
                });
    }

}
