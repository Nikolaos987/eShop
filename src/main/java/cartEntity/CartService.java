package cartEntity;

import io.vertx.core.Future;
import userEntity.UsersStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CartService {

    private final CartsStore cartsStore;
    private final UsersStore usersStore;

    public CartService(CartsStore cartsStore, UsersStore usersStore) {
        this.cartsStore = cartsStore;
        this.usersStore = usersStore;
    }


    public Future<Void> addItem(UUID uid, UUID pid, int quantity) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null) {
                        return cartsStore.findCart(uid)
                                .compose(cart -> {
                                    if (cart.items().iterator().next() == null) {
                                        Collection<CartItem> items = new ArrayList<>();
                                        items.add(new CartItem(UUID.randomUUID(), pid, quantity));
                                        return cartsStore.insert(new Cart(cart.cid(), cart.uid(), cart.dateCreated(), items));
                                    }
                                    return cartsStore.update(cart, quantity);
                                });
                    }
                    return Future.failedFuture(new IllegalArgumentException("user does not exist"));
                });

    }

    public Future<Void> buyCart(UUID uid) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return cartsStore.deleteCart(uid);
                    return Future.failedFuture(new IllegalArgumentException("user does not exist"));
                });
    }
}
