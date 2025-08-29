package uy.com.inventory.sync_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.com.inventory.sync_service.kafka.InventoryEventsHandlers;
import uy.com.inventory.sync_service.view.State;

@RestController
@RequestMapping("/state")
@AllArgsConstructor
public class StateController {
    private final InventoryEventsHandlers consumer;

    @GetMapping("/{storeId}/{itemId}")
    public ResponseEntity<Object> get(@PathVariable String storeId, @PathVariable String itemId) {
        State state = consumer.get(storeId, itemId);
        if (state == null) return ResponseEntity.ok(java.util.Map.of("storeId", storeId, "itemId", itemId, "message", "no data yet"));

        return ResponseEntity.ok(state);
    }
}
