package userEntity;

import cartEntity.Cart;
import cartEntity.CartsStore;
import io.vertx.core.Future;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

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
        return userStore.findUser(uid)
                .otherwiseEmpty()
                .compose(user -> {
                    if (user != null) {
                        return cartsStore.deleteCart(uid)
                                .compose(result -> userStore.deleteUser(user));
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("User was not found"));
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