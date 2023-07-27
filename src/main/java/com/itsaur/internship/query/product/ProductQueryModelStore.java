package com.itsaur.internship.query.product;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.UUID;

public interface ProductQueryModelStore {
    Future<ProductsQueryModel.ProductQueryModel> findProductById(UUID pid);
    Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProductsByName(String regex);
    Future<ArrayList<ProductsQueryModel.ProductQueryModel>> findProducts();
}
