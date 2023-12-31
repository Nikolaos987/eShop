package com.itsaur.internship.cartEntity;

import io.vertx.core.Future;
import com.itsaur.internship.productEntity.ProductsStore;
import com.itsaur.internship.userEntity.UsersStore;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
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


    public Future<UUID> addItem(UUID uid, UUID pid, int quantity) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return cartsStore.findCart(uid)
                                .compose(cart -> {
                                    List<CartItem> items = cart.items();
                                    if (items.stream().anyMatch(cartItem -> cartItem.pid().equals(pid))) {  // if the product already exists in your cart
                                        CartItem cartItem = cart.items().get(items.indexOf(new CartItem(    // find the item in the cart -->
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

    public Future<UUID> buyCart(UUID uid) {
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
    public Future<Void> deleteCartItems(UUID pid) {
//        return cartsStore.findCarts()
//                .otherwiseEmpty()
//                .compose(carts -> {
//                    if (carts.size() == 0) {
//                        return Future.succeededFuture();
//                    } else {
//                        List<Future<Void>> futureList = carts
//                                .stream()
//                                .map(cart -> {
//                                    cart.items().add(new CartItem(cart.items().get(0), ca))
//                                    return cartsStore.update(cart));
//                                })
//                    }
//                })

        return productsStore.findProduct(pid)
                .compose(product -> {
                    if (product != null)
                        return cartsStore.findCarts()
                                .compose(carts -> {
                                    return deleteNext(carts, 0, pid);
                                    /*carts.forEach(cart -> {
                                        UUID itemId = cart.items().stream().filter(item -> item.pid().equals(pid)).findAny().get().itemId();
                                        cart.items().add(new CartItem(itemId, pid, 0));
                                        cartsStore.update(cart);
                                    });*/
                                });
                    return Future.failedFuture(new IllegalArgumentException("this product does not exist"));
                });
    }

    public Future<Void> deleteNext(List<Cart> carts, int position, UUID pid) {
        Cart cart = carts.get(position);
        UUID itemId = cart.items().stream().filter(item -> item.pid().equals(pid)).findAny().get().itemId();
        cart.items().add(new CartItem(itemId, pid, 0));
        return cartsStore.update(cart)
                .compose(r -> {
                    if (position+1 < carts.size())
                        return deleteNext(carts, position + 1, pid);
                    else
                        return Future.succeededFuture();
                });
    }
}
