package uy.com.inventory.inventory_service.dto;

import jakarta.persistence.*;
import lombok.*;
import uy.com.inventory.inventory_service.domain.Stock;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockDTO {
    Long id;
    String storeId;
    String itemId;
    int available;
    int reserved;
    long version;

    public StockDTO(Stock stock) {
        this.id = stock.getId();
        this.storeId = stock.getStoreId();
        this.itemId = stock.getItemId();
        this.available = stock.getAvailable();
        this.reserved = stock.getReserved();
        this.version = stock.getVersion();
    }
}
