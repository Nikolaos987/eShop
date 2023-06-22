package com.itsaur.internship;

import io.vertx.core.Future;

import java.util.prefs.Preferences;

public record User(String username, String password) {

    public static Preferences pref = Preferences.userRoot().node("rememberUser");
    public boolean matches(String otherPassword) {
        return password.equals(otherPassword);
    }

    public void remember(User user) {
        pref.put("username", user.username);
        pref.put("password", user.password);
    }

    public static Future<Void> forget() {
        pref.remove("username");
        pref.remove("password");
        return Future.succeededFuture();
    }

    public static User getUser() {
        String usr = null;
        String pass = null;
        return new User(User.pref.get("username", usr), User.pref.get("password", pass));
    }
}