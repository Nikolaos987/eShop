package com.itsaur.internship.query.product;

import com.itsaur.internship.productEntity.Category;

import java.util.List;
import java.util.UUID;

public record ProductsQueryModel(List<ProductQueryModel> products) {
    public record ProductQueryModel(UUID pid, String name, String image, String description, double price, int quantity, String brand, Category category) {
    }
}