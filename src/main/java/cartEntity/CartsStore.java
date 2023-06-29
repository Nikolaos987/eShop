package cartEntity;

import io.vertx.core.Future;

import java.util.Collection;
import java.util.UUID;

public interface CartsStore {

    Future<Void> insert(CartItem cartItem, UUID pid);

    Future<Void> insert(Cart cart);

    Future<CartItem> findCartItem(UUID itemid);

    Future<CartItem> findCartItem(UUID uid, UUID pid);

    Future<Collection<CartItem>> findCartItems(UUID uid);

    Future<Cart> findCart(UUID uid);

    Future<Void> removeCartItem(CartItem item);

    Future<Void> updateCartItem(CartItem item, int quantity);

}
