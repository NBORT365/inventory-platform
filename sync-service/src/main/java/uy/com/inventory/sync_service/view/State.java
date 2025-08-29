package uy.com.inventory.sync_service.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class State {
    String storeId;
    String itemId;
    int available;
    int reserved;
}