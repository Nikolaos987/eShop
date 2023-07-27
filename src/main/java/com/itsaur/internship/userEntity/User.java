package com.itsaur.internship.userEntity;

import java.util.UUID;

public record User(UUID uid, String username, String password) {

    public boolean matches(String otherPassword) {
        return password.equals(otherPassword);
    }

}