package com.itsaur.internship.userEntity;

import com.itsaur.internship.cartEntity.Cart;
import com.itsaur.internship.cartEntity.CartItem;
import com.itsaur.internship.cartEntity.CartsStore;
import io.vertx.core.Future;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService {

    private final UsersStore userStore;
    private final CartsStore cartsStore;

    public UserService(UsersStore userStore, CartsStore cartsStore) {
        this.userStore = userStore;
        this.cartsStore = cartsStore;
    }

    public Future<User> login(String username, String password) {
        return userStore.findUser(username)
                .compose(user -> {
                    if (user.matches(password)) {
                        return Future.succeededFuture(user);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("Invalid password"));
                    }
                });
    }

    public Future<Void> register(String username, String password) {
        return userStore.findUser(username)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user == null) {
                        UUID uuid = UUID.randomUUID();
                        return userStore.insert(new User(uuid, username, password))
                                .compose(newUser -> cartsStore.insert(new Cart(UUID.randomUUID(), uuid, LocalDateTime.now(), new ArrayList<>())));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User already exists"));
                    }
                });
    }

    public Future<Void> delete(UUID uid) {
        return deleteCartItems(uid)
                .compose(r -> this.cartsStore.deleteCart(uid)
                        .compose(res -> userStore.deleteUser(uid)));

//        return userStore.findUser(uid)
//                .otherwiseEmpty()
//                .compose(user -> {
//                    if (user != null) {
//                        return cartsStore.deleteCart(uid)
//                                .compose(result -> userStore.deleteUser(user));
//                    } else {
//                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
//                    }
//                });
    }

    public Future<Void> deleteCartItems(UUID uid) {
        return this.cartsStore.findCart(uid)
                .otherwiseEmpty()
                .compose(cart -> {
                    if (cart == null) {
                        System.out.println("There is no cart for this user id");
                        return Future.succeededFuture();
                    } else {
                        List<Future<Void>> futureList = cart.items()
                                .stream()
                                .map(item -> {
                                    System.out.println(item.itemId());
                                    cart.items().add(new CartItem(item.itemId(), item.pid(), 0));
                                    return cartsStore.update(cart);
                                }).collect(Collectors.toList());
                        return Future.all(futureList)
                                .onFailure(e -> {
                                    e.printStackTrace();
                                }).compose(q -> {
                                    System.out.println(q);
                                    return Future.succeededFuture();
                                });
                    }
                });
    }

    public Future<Void> update(UUID uid, String currentPassword, String newPassword) {
        return userStore.findUser(uid)
                .compose(user -> {
                    if (user.matches(currentPassword)) {
                        return userStore.updateUser(user.username(), newPassword);
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("passwords do not match"));
                    }
                });
    }

}