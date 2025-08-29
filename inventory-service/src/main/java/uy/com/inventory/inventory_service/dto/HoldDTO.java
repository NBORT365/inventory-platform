package uy.com.inventory.inventory_service.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.com.inventory.inventory_service.domain.Hold;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HoldDTO {
    Long id;
    String holdId;
    String storeId;
    String itemId;
    int qty;
    Instant expiresAt;
    String status;

    public HoldDTO (Hold hold) {
        this.id = hold.getId();
        this.holdId = hold.getHoldId();
        this.storeId = hold.getStoreId();
        this.itemId = hold.getItemId();
        this.qty = hold.getQty();
        this.expiresAt = hold.getExpiresAt();
        this.status = hold.getStatus();
    }

}