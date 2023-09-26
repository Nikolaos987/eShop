package com.itsaur.internship.query.relatedProducts;

import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface RelatedProductsQueryModelStore {
    Future<RelatedProductsQueryModel> getRelatedProducts(UUID r_pid);
}
