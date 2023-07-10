package query.cart;

import io.vertx.core.Future;

import java.util.UUID;

public interface CartQueryModelStore {
    Future<CartQueryModel> findByUserId(UUID uid);
}
