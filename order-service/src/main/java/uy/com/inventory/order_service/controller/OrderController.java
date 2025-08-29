package uy.com.inventory.order_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import uy.com.inventory.common.command.ConfirmReservation;
import uy.com.inventory.common.command.ReleaseReservation;
import uy.com.inventory.common.command.ReserveStock;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/inventory/{storeId}/items/{itemId}")
@AllArgsConstructor
public class OrderController {
    private final long EXPIRATION_IN_SECONDS = 900;
    KafkaTemplate<String, Object> kafka;


    @PostMapping("/reserve")
    public ResponseEntity<Object> reserve(@PathVariable String storeId, @PathVariable String itemId,@Valid @RequestBody QtyReq req) {
        String holdId = "H-" + UUID.randomUUID();
        ReserveStock cmd = new ReserveStock(UUID.randomUUID().toString(), storeId, itemId, req.qty(), holdId, Instant.now().plusSeconds(EXPIRATION_IN_SECONDS));
        kafka.send("inventory-commands", storeId + ":" + itemId, cmd);
        return ResponseEntity.status(201).body(new ReserveResp(holdId, cmd.getTtlUntil()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@PathVariable String storeId, @PathVariable String itemId, @Valid @RequestBody HoldReq req) {
        ConfirmReservation cmd = new ConfirmReservation(UUID.randomUUID().toString(), storeId, itemId, req.holdId());
        kafka.send("inventory-commands", storeId + ":" + itemId, cmd);
        return ResponseEntity.accepted().body("Confirmation processed successfully");
    }

    @PostMapping("/release")
    public ResponseEntity<String> release(@PathVariable String storeId, @PathVariable String itemId, @Valid @RequestBody HoldReq req) {
        ReleaseReservation cmd = new ReleaseReservation(UUID.randomUUID().toString(), storeId, itemId, req.holdId());
        kafka.send("inventory-commands", storeId + ":" + itemId, cmd);
        return ResponseEntity.accepted().body("Release processed successfully");
    }

    public record QtyReq(@NotNull @Min(value = 1, message = "Items quantity can't be less than zero") int qty) {}
    public record HoldReq(@NotBlank(message="holdId is required") String holdId) {}
    public record ReserveResp(String holdId, Instant expiresAt) {}
}
