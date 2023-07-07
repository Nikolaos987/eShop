package query;

import java.util.List;
import java.util.UUID;

public record CartQueryModel(List<ProductQueryModel> products) {
    public record ProductQueryModel(UUID pid, String name, double price, int quantity) {
    }
}
