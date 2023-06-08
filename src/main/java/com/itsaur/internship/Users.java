package com.itsaur.internship;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

public final class Users {
    private String username;
    private String password;

    JsonObject jsonObject = new JsonObject();
    JsonArray jsonArray = new JsonArray();

    public Users() {

    }

    public Users(String username, String password) {
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonArray.add(jsonObject);
    }

    public void addUser(String username, String password) {
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonArray.add(jsonObject);
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    public void deleteUser(JsonObject user) {
        jsonArray.remove(user);
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Users) obj;
        return Objects.equals(this.username, that.username) &&
                Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "Users[" +
                "username=" + username + ", " +
                "password=" + password + ']';
    }

}
