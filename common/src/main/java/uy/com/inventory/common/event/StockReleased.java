package uy.com.inventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReleased {
    String eventId;
    String storeId;
    String itemId;
    int qty;
    String holdId;
}
