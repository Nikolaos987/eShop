package cartEntity;

import io.vertx.core.Future;
import productEntity.ProductsStore;
import userEntity.UsersStore;

import java.util.ArrayList;
import java.util.UUID;

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
                                    if (items.stream().anyMatch(cartItem -> cartItem.pid().equals(pid))) {  // if the product already exists in your cart
                                        CartItem cartItem = cart.items().get(items.indexOf(new CartItem(    // find the item in the cart
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().itemId(), // the item id of the item with the given pid
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().pid(),    // the pid of the item with the given pid
                                                items.stream().filter(item -> item.pid().equals(pid)).findAny().get().quantity())));    // the quantity of the item with the given pid
//                                        cart.items().set(items.indexOf(cartItem), new CartItem(cartItem.itemId(), cartItem.pid(), quantity));   // change the quantity of the item
                                        ArrayList<CartItem> item = new ArrayList<>();
                                        item.add(new CartItem(cartItem.itemId(), pid, quantity));
                                        return cartsStore.update(new Cart(cart.cid(), cart.uid(), cart.dateCreated(), item));
                                    } else {    // if the product does not exist in your cart
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
                        return productsStore.updateProducts(uid)
                                .compose(r -> cartsStore.findCart(uid)
                                        .compose(cart -> {
                                            cart.items().replaceAll(item -> new CartItem(item.itemId(), item.pid(), 0));
                                            return cartsStore.update(cart);
                                        }));
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
