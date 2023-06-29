package productEntity;

import io.vertx.core.Future;

import java.util.Collection;
import java.util.UUID;

public class ProductService {

    private final ProductsStore store;

    public ProductService(ProductsStore store) {
        this.store = store;
    }

    public Future<Product> product(UUID productId) {
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

    public Future<Collection<Product>> products(String name) {
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

    public Future<Void> insert(String name, String description, double price, int quantity, String brand, String category) {
        return store.insert(new Product(UUID.randomUUID(), name, description, price, quantity, brand, category))
                .compose(r -> Future.succeededFuture());
    }

    public Future<Void> delete(UUID pid) {
        return store.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return store.deleteProduct(pid);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> update(UUID pid, double price) {
        return store.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return store.updateProduct(pid, price);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

}
