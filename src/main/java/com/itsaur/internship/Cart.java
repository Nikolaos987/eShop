package com.itsaur.internship;

import java.time.LocalDateTime;
import java.util.UUID;

public record Cart(UUID cid, UUID uid, LocalDateTime dateCreated) {

}
