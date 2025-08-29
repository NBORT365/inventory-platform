package uy.com.inventory.common.command;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservation {
    String eventId;
    String storeId;
    String itemId;
    String holdId;
}
