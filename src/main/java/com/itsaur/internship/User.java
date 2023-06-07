package com.itsaur.internship;

public record User(String username, String password) {
    public boolean matches(String otherPassword) {
        return password.equals(otherPassword);
    }
}