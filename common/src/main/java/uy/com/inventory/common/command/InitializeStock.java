package uy.com.inventory.common.command;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitializeStock {
    String eventId;
    String storeId;
    String itemId;
    int available;
}
