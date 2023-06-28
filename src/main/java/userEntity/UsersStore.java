package userEntity;

import io.vertx.core.Future;

import java.util.UUID;

public interface UsersStore {
    Future<Void> insert(User user);

    Future<User> findUser(String username);

    Future<User> findUser(UUID uid);

    Future<Void> deleteUser(User user);

    Future<Void> updateUser(String username, String password);

}
