package cartEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public record Cart(UUID cid, UUID uid, LocalDateTime dateCreated, Collection<CartItem> items) {

}
