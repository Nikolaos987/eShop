package productEntity;

import io.vertx.core.Future;

import java.util.Collection;
import java.util.UUID;

public class ProductService {

    private final ProductsStore store;

    public ProductService(ProductsStore store) {
        this.store = store;
    }

    public Future<Product> findProduct(UUID productId) {
        return store.findProduct(productId)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null) {
                        return Future.succeededFuture(product);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<Collection<Product>> findProducts(String name) {
        return store.findProduct(name)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products.size() != 0) {
                        return Future.succeededFuture(products);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("product not found"));
                    }
                });
    }

    public Future<Void> addProduct(String name, String description, double price, int quantity, String brand, Category category) {

        return store.insert(new Product(UUID.randomUUID(), name, description, price, quantity, brand, category))
                .compose(r -> Future.succeededFuture());
    }

    public Future<Void> deleteProduct(UUID pid) {
        return store.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return store.deleteProduct(pid);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> updateProduct(UUID pid, double price) {
        return store.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return store.updateProduct(pid, price);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

}
