package query.cart;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface CartQueryModelStore {
    Future<ArrayList<CartQueryModel.CartItemQueryModel>> findByUserId(UUID uid);
}
