package com.itsaur.internship.query.product;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.*;

public interface ProductQueryModelStore {
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<ProductsQueryModel> findProductsByName(String regex, int from, int range);
    Future<ProductsQueryModel> findProductsByCategories(String[] category, int from, int range);
    Future<ProductsQueryModel> findFilteredProductsByCategories(String regex, String[] category, int from, int range);
    Future<ProductsQueryModel> findProducts(int from, int range);
    Future<Buffer> findImageById(UUID pid);
    Future<CategoriesQueryModel> fetchCategories();
}
