package productEntity;

import cartEntity.CartsStore;
import io.vertx.core.Future;

import java.util.Collection;
import java.util.UUID;

public class ProductService {

    private final ProductsStore productsStore;
    private final CartsStore cartsStore;

    public ProductService(ProductsStore productsStore, CartsStore cartsStore) {
        this.productsStore = productsStore;
        this.cartsStore = cartsStore;
    }

    public Future<Product> findProduct(UUID productId) {
        return productsStore.findProduct(productId)
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
        return productsStore.findProducts(name)
                .otherwiseEmpty()
                .compose(products -> {
                    if (products.size() != 0)
                        return Future.succeededFuture(products);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> addProduct(String name, String description, double price, int quantity, String brand, Category category) {
        return productsStore.findProduct(name)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product == null)
                        return productsStore.insert(new Product(UUID.randomUUID(), name, description, price, quantity, brand, category));
                    return Future.failedFuture(new IllegalArgumentException("product with this name already exists!"));
                });


    }

    public Future<Void> deleteProduct(UUID pid) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return cartsStore.removeCartItems(pid)
                                .compose(result -> productsStore.deleteProduct(pid));
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

    public Future<Void> updateProduct(UUID pid, double price) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return productsStore.updateProduct(pid, price);
                    return Future.failedFuture(new IllegalArgumentException("product not found"));
                });
    }

}
