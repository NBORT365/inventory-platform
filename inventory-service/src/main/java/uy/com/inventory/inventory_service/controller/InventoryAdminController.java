package uy.com.inventory.inventory_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.inventory.common.command.InitializeStock;
import uy.com.inventory.inventory_service.service.InventoryAdminService;
import uy.com.inventory.inventory_service.service.InventoryService;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class InventoryAdminController {
    private final InventoryAdminService adminService;
    private final InventoryService inventoryService;

    public record InitReq(@NotEmpty @NotNull String storeId, @NotEmpty @NotNull String itemId, @Min(value = 1, message = "Items quantity can't be less than zero") int available) {}

    @PostMapping("/newSKU")
    public ResponseEntity<?> initialize(@Valid @RequestBody InitReq req) {
        if(inventoryService.existsByStoreIdAndItemId(req.storeId, req.itemId))
            return ResponseEntity.status(400).body("The item already exists for this store");

        String evtId = java.util.UUID.randomUUID().toString();
        adminService.createSKU(new InitializeStock(evtId, req.storeId(), req.itemId(), req.available()));
        return ResponseEntity.status(201).body("The sku has been created.");
    }

    @DeleteMapping("/delete/{storeId}/{itemId}")
    public ResponseEntity<?> deleteById(@PathVariable String storeId, @PathVariable String itemId) {
        if(inventoryService.existsByStoreIdAndItemId(storeId, itemId)){
            adminService.deleteByStoreIdAndItemId(storeId, itemId);
            return ResponseEntity.status(200).body("The item deleted successfully");
        }else
            return ResponseEntity.status(400).body("The item does not exist for this store");
    }
}
