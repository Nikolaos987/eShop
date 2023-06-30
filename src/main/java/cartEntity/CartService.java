package cartEntity;

import io.vertx.core.Future;
import productEntity.Product;
import productEntity.ProductsStore;
import userEntity.User;
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

    public Future<CartItem> showCartItem(UUID itemid) {
        return cartsStore.findCartItem(itemid);
    }

    public Future<ArrayList<CartItem>> showCartItems(UUID uid) {
        return cartsStore.findCartItems(uid);
    }

    public Future<Void> addItem(UUID uid, UUID pid, int quantity) {
        return userExists(uid)
                .compose(user -> productExists(pid)
                        .compose(product -> cartsStore.findCartItem(uid, pid)
                                .otherwiseEmpty()
                                .compose(item -> {
                                    if (item == null) {
                                        return cartsStore.findCart(uid)
                                                .compose(cart -> cartsStore.insert(new CartItem(UUID.randomUUID(), pid, quantity), cart.cid()));
                                    }
                                    return cartsStore.updateCartItem(item, quantity);
                                })));
    }

    public Future<Void> buyCart(UUID uid) {
        return userExists(uid)
                .compose(result -> cartsStore.findCartItems(uid)
                        .compose(items -> cartsStore.removeCartItems(items)
                                .compose(result2 -> productsStore.updateProducts(items))));
    }

    public Future<User> userExists(UUID uid) {
        return usersStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null)
                        return Future.succeededFuture(user);
                    return Future.failedFuture(new IllegalArgumentException("user does not exist"));
                });
    }

    Future<Product> productExists(UUID pid) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return Future.succeededFuture(product);
                    return Future.failedFuture(new IllegalArgumentException("product does not exist"));
                });
    }

}
