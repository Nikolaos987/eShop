package com.itsaur.internship.query.product;

import java.util.List;

public record CategoriesQueryModel(List<CategoryQueryModel> category) {
    public record CategoryQueryModel(String category) {
    }
}
