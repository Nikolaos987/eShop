package com.itsaur.internship.query.cart;

import java.util.List;
import java.util.UUID;

public record CartQueryModel(List<CartItemQueryModel> cartItem, Double totalPrice) {

    public record CartItemQueryModel(UUID pid, String name, double price, int quantity) {
    }
}
