package com.itsaur.internship.query.product;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.UUID;

public interface ProductQueryModelStore {
    Future<Integer> productsCount();
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex);
    Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProducts(int from, int range);
    Future<Buffer> findImageById(UUID pid);
}
