package cartEntity;

import io.vertx.core.Future;
import productEntity.ProductsStore;
import userEntity.UsersStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class CartService {

    private final CartsStore cartsStore;
    private final ProductsStore productsStore;
    private final UsersStore usersStore;

    public CartService(CartsStore cartsStore, ProductsStore productsStore, UsersStore usersStore) {
        this.cartsStore = cartsStore;
        this.productsStore = productsStore;
        this.usersStore = usersStore;
    }


    public Future<Void> addItem(UUID uid, UUID pid, int quantity) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return cartsStore.findCart(uid)
                                .compose(cart -> {
                                    ArrayList<CartItem> items = cart.items();
                                    if (items.stream().anyMatch(cartItem -> cartItem.pid().equals(pid))) {
                                        CartItem cartItem = cart.items().get(items.indexOf(new CartItem(
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().itemId(),
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().pid(),
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().quantity())));
                                        cart.items().set(items.indexOf(cartItem), new CartItem(cartItem.itemId(), cartItem.pid(), quantity));
                                        ArrayList<CartItem> item = new ArrayList<>();
                                        item.add(new CartItem(cartItem.itemId(), pid, quantity));
                                        return cartsStore.update(new Cart(cart.cid(), cart.uid(), cart.dateCreated(), item));
                                    } else {
                                        cart.items().add(new CartItem(UUID.randomUUID(), pid, quantity));
                                        return cartsStore.update(cart);
                                    }
                                });
                    return Future.failedFuture(new IllegalArgumentException("user does not exist"));
                });
    }

    public Future<Void> buyCart(UUID uid) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return cartsStore.deleteCart(uid); //lathos. productsStore.update
                    return Future.failedFuture(new IllegalArgumentException("user does not exist"));
                });
    }

    // TODO: 5/7/23
//    public Future<Void> deleteCartItems(UUID pid) {
//        return productsStore.findProduct(pid)
//                .compose(product -> {
//                    if (product != null)
//                        return
//                });
//    }
}
