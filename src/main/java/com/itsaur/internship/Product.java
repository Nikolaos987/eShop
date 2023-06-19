package com.itsaur.internship;

import java.util.UUID;

public record Product(UUID productId, String name, String description, double price, int quantity, String brand, String category){

}
