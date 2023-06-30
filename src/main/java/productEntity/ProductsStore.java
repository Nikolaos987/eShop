package productEntity;

import cartEntity.CartItem;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface ProductsStore {

    Future<Void> insert(Product product);

    Future<Product> findProduct(UUID pid);

    Future<Product> findProduct(String name);

    Future<Collection<Product>> findProducts(String filter);

    Future<Void> deleteProduct(UUID pid);

    Future<Void> updateProduct(UUID pid, double price);

    Future<Void> updateProducts(ArrayList<CartItem> items);

//    Future<JsonArray> filter(double price, String brand, String category);

}
