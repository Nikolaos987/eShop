package com.itsaur.internship.query.product;

import com.beust.ah.A;
import com.itsaur.internship.productEntity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductsQueryModel {
    List<ProductQueryModel> products;
    Integer count;

    public ProductsQueryModel() {
    }
    public ProductsQueryModel(List<ProductQueryModel> products, Integer count) {
        this.products = products;
        this.count = count;
    }

    public void setProducts(List<ProductQueryModel> products) {
        this.products = products;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ProductQueryModel> getProducts() {
        return products;
    }

    public Integer getCount() {
        return count;
    }


    public record ProductQueryModel(UUID pid, String name, String description, double price, int quantity, String brand, Category category) {}
}