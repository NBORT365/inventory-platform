package uy.com.inventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReserved {
    String eventId;
    String storeId;
    String itemId;
    int qty;
    String holdId;
    Instant expiresAt;
}
