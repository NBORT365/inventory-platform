package uy.com.inventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSnapshotted {
    String eventId;
    String storeId;
    String itemId; int available;
    int reserved;
}
