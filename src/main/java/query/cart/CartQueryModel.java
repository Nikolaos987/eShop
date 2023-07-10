package query.cart;

import java.util.List;
import java.util.UUID;

public record CartQueryModel(List<CartItemQueryModel> items) {
    public record CartItemQueryModel(UUID pid, String name, double price, int quantity) {
    }
}
