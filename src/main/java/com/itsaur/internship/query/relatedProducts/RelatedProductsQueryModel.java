package com.itsaur.internship.query.relatedProducts;

import com.itsaur.internship.productEntity.Product;

import java.util.List;
import java.util.UUID;

public record RelatedProductsQueryModel(List<Product> products) {
}
