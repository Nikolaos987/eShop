package com.itsaur.internship;

import java.util.UUID;

public record CartItem(UUID itemId, UUID cid, UUID pid, int quantity) {

}
