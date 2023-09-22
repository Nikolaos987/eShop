package com.itsaur.internship.query.product;

import java.util.List;
import java.util.UUID;

public record ProductsQueryModel(List<ProductQueryModel> products, Integer count) {
    public record ProductQueryModel(UUID pid, String name, String description, double price, int quantity, String brand, String category) {
    }
}