package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartItem;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ProductsStore {

    Future<Void> insertMultiple(int size);

    Future<Product> insert(Product product);

    Future<Product> findProduct(UUID pid);

    Future<Product> findProduct(String name);

    Future<Void> deleteProduct(UUID pid);

    Future<Void> updateProduct(Product product);

    Future<Void> insertImage(UUID pid, Buffer buffer);

    Future<Void> updateProducts(ArrayList<CartItem> items);

    Future<Void> updateProducts(UUID uid);

    Future<UUID> addRelatedProduct(UUID r_pid, UUID to_pid);

    Future<UUID> findRelatedProduct(UUID r_pid, UUID to_pid);
}
