package com.itsaur.internship.query.product;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ProductQueryModelStore {
    Future<Integer> productsCount();
    Future<Integer> filteredProductsCount(String regex);
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex, int from, int range);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProducts(int from, int range);
    Future<Buffer> findImageById(UUID pid);
}
