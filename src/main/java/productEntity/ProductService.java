package productEntity;

import cartEntity.CartService;
import cartEntity.CartsStore;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.Collection;
import java.util.UUID;

public class ProductService {

    private final ProductsStore productsStore;
    private final CartService cartService;

    public ProductService(ProductsStore productsStore, CartService cartService) {
        this.productsStore = productsStore;
        this.cartService = cartService;
    }

    public Future<Buffer> findProductImage(UUID pid) {
        return productsStore.findProductImage(pid)
                .compose(Future::succeededFuture);
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

    public Future<Void> addProduct(String name, String imagePath, String description, double price, int quantity, String brand, Category category) {
        return productsStore.findProduct(name)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product == null)
                        return productsStore.insert(new Product(UUID.randomUUID(), name, imagePath, description, price, quantity, brand, category));
                    return Future.failedFuture(new IllegalArgumentException("product with this name already exists!"));
                });
    }

    public Future<Void> deleteProduct(UUID pid) {
        return productsStore.findProduct(pid)
                .otherwiseEmpty()
                .compose(product -> {
                    if (product != null)
                        return cartService.deleteCartItems(pid)
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
