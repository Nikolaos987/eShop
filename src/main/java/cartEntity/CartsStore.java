package cartEntity;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface CartsStore {

    Future<Void> insert(Cart cart);

    Future<Cart> findCart(UUID uid);

    Future<Void> deleteCart(UUID uid);

    Future<Void> update(Cart cart, int quantity);

}
