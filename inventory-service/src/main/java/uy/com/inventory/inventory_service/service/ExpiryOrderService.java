package uy.com.inventory.inventory_service.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uy.com.inventory.common.event.StockReleased;
import uy.com.inventory.inventory_service.repository.HoldRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExpiryOrderService {
    private final HoldRepository holdRepository;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafka;

    @Scheduled(fixedDelay = 30000)
    public void expireOrders() {
        Instant now = Instant.now();
        holdRepository.findAll()
                .stream()
                .filter( h -> "ACTIVE".equals(h.getStatus()) && h.getExpiresAt() != null && h.getExpiresAt().isBefore(now))
                .forEach( h -> {
                    inventoryService.release(h.getHoldId());
                    kafka.send("inventory-events", h.getStoreId() + ":" + h.getItemId(),
                            new StockReleased(UUID.randomUUID().toString(), h.getStoreId(), h.getItemId(), h.getQty(), h.getHoldId()));
                });
    }
}
