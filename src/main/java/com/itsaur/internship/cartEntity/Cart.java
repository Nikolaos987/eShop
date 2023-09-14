package com.itsaur.internship.cartEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Cart(UUID cid, UUID uid, LocalDateTime dateCreated, List<CartItem> items) {

}
