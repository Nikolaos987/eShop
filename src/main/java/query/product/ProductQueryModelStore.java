package query.product;

import io.vertx.core.Future;

import java.util.UUID;

public interface ProductQueryModelStore {
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<ProductsQueryModel> findProductsByName(String regex);
    Future<ProductsQueryModel> findProducts();
}
