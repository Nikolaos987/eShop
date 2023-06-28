package productEntity;

import java.util.UUID;

public record Product(UUID pid, String name, String description, double price, int quantity, String brand, String category){
}
