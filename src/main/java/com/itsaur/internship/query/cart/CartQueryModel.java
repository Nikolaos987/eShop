package com.itsaur.internship.query.cart;

import java.util.List;
import java.util.UUID;

public class CartQueryModel {
    List<CartItemQueryModel> items;
    Double totalPrice;

    public CartQueryModel() {
    }
    public CartQueryModel(List<CartItemQueryModel> items, Double totalPrice) {
        this.items = items;
        this.totalPrice = totalPrice;
    }


    public void setItems(List<CartItemQueryModel> items) {
        this.items = items;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartItemQueryModel> getItems() {
        return items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public record CartItemQueryModel(UUID pid, String name, double price, int quantity) {
    }
}
