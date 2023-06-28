package userEntity;

import io.vertx.core.Future;

import java.util.UUID;
import java.util.prefs.Preferences;

public record User(UUID uid, String username, String password) {

    public boolean matches(String otherPassword) {
        return password.equals(otherPassword);
    }

}