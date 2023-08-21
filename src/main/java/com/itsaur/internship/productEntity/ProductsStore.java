package com.itsaur.internship.productEntity;

import com.itsaur.internship.cartEntity.CartItem;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface ProductsStore {

    Future<Product> insert(Product product);

    Future<Product> findProduct(UUID pid);

    Future<Product> findProduct(String name);

    Future<Void> deleteProduct(UUID pid);

    Future<Void> updateProduct(Product product);

    Future<Void> updateProducts(ArrayList<CartItem> items);

    Future<Void> updateProducts(UUID uid);

}
