package com.itsaur.internship.query.cart;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface CartQueryModelStore {
//    Future<List<CartQueryModel.CartItemQueryModel>> findByUserId(UUID uid);
    Future<CartQueryModel> findByUserId(UUID uid);

    Future<Double> totalPrice(UUID uid);
}
