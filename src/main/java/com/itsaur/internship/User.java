package com.itsaur.internship;

import io.vertx.core.Future;

import java.util.prefs.Preferences;

public record User(String username, String password) {

    public boolean matches(String otherPassword) {
        return password.equals(otherPassword);
    }

}