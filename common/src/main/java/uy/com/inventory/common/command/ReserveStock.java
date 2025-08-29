package uy.com.inventory.common.command;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveStock {
    String eventId;
    String storeId;
    String itemId;
    int qty;
    String holdId;
    Instant ttlUntil;
}
