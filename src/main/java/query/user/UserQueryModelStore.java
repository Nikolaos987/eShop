package query.user;

import io.vertx.core.Future;
import query.product.ProductsQueryModel;

import java.util.UUID;

public interface UserQueryModelStore {
    Future<UserQueryModel> findUserById(UUID uid);
}
