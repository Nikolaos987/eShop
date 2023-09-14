package com.itsaur.internship.cartEntity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface CartsStore {

    Future<Void> insert(Cart cart);

    Future<Cart> findCart(UUID uid);

    Future<List<Cart>> findCarts();

    Future<Void> deleteCart(UUID uid);

    Future<UUID> update(Cart cart);

}
