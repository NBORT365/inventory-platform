package uy.com.inventory.inventory_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.com.inventory.inventory_service.dto.HoldDTO;
import uy.com.inventory.inventory_service.dto.StockDTO;
import uy.com.inventory.inventory_service.exceptions.NotFoundException;
import uy.com.inventory.inventory_service.service.InventoryService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@AllArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{storeId}/items/{itemId}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String storeId, @PathVariable String itemId) {
        StockDTO stock = inventoryService.findByStoreIdAndItemId(storeId, itemId);

        if (stock == null)
                throw new NotFoundException("Item not found for %s:%s".formatted(storeId, itemId));

        return ResponseEntity.ok(
                Map.of(
                "storeId", stock.getStoreId(),
                "itemId", stock.getItemId(),
                "available", stock.getAvailable(),
                "reserved", stock.getReserved()
        ));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<StockDTO>> getItemsByStore(@PathVariable String storeId) {
        return ResponseEntity.ok(inventoryService.findByStore(storeId));
    }

    @GetMapping("orders/{storeId}")
    public ResponseEntity<List<HoldDTO>> getOrdersByStore(@PathVariable String storeId) {
        return ResponseEntity.ok(inventoryService.findHoldingOrdersByStore(storeId));
    }
}
