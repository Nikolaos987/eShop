package cartEntity;

import java.util.UUID;

public record CartItem(UUID itemId, UUID pid, int quantity) {

}
