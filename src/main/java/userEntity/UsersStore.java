package userEntity;

import io.vertx.core.Future;

import java.util.UUID;

public interface UsersStore {
    Future<Void> insert(String username, String password);

    Future<User> findUser(String username);

    Future<User> findUser(UUID uid);

    Future<Void> deleteUser(User user);

    Future<Void> updateUser(String username, String password);

}
