package cartEntity;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface CartsStore {

    Future<Void> insert(CartItem cartItem, UUID pid);

    Future<Void> insert(Cart cart);

    Future<CartItem> findCartItem(UUID itemid);

    Future<CartItem> findCartItem(UUID uid, UUID pid);

    Future<ArrayList<CartItem>> findCartItems(UUID uid);

    Future<Cart> findCart(UUID uid);

    Future<Void> deleteCart(UUID uid);

    Future<Void> removeCartItems(ArrayList<CartItem> items);

    Future<Void> removeCartItems(UUID pid);

    Future<Void> updateCartItem(CartItem item, int quantity);

}
