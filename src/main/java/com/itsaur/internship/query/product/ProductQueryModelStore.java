package com.itsaur.internship.query.product;

import com.itsaur.internship.productEntity.Category;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ProductQueryModelStore {
    Future<Integer> productsCount();
    Future<Integer> filteredProductsCount(String regex);
    Future<Integer> productsCategoryCount(String category); // TODO: REMOVE
    Future<Integer> productsCategoriesCount(String[] category);
    Future<Integer> productsFilteredCategoriesCount(String regex, String[] category);
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex, int from, int range);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByCategory(String category, int from, int range);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProductsByCategories(String[] category, int from, int range);
    Future<List<ProductsQueryModel.ProductQueryModel>> findFilteredProductsByCategories(String regex, String[] category, int from, int range);
    Future<List<ProductsQueryModel.ProductQueryModel>> findProducts(int from, int range);
    Future<Buffer> findImageById(UUID pid);
}
