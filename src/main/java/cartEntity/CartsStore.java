package cartEntity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.UUID;

public interface CartsStore {

    Future<Void> checkQuantity(UUID id, int quantity);

    Future<Void> addToCart(UUID uid, UUID pid, int quantity);

    Future<Boolean> findCart(UUID id);

    Future<Boolean> findItem(UUID uid, UUID pid);

    Future<Void> removeFromCart(UUID uid, UUID pid, int quantity);

    Future<JsonArray> cart(UUID uid);

    Future<Void> buy(UUID uid);
}
