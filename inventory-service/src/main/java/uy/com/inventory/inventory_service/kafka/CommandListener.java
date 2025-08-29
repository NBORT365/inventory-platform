package uy.com.inventory.inventory_service.kafka;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uy.com.inventory.common.command.ConfirmReservation;
import uy.com.inventory.common.command.ReleaseReservation;
import uy.com.inventory.common.command.ReserveStock;
import uy.com.inventory.common.event.StockDecremented;
import uy.com.inventory.common.event.StockReleased;
import uy.com.inventory.common.event.StockReserved;
import uy.com.inventory.inventory_service.domain.ProcessedEvent;
import uy.com.inventory.inventory_service.dto.HoldDTO;
import uy.com.inventory.inventory_service.repository.ProcessedEventRepository;
import uy.com.inventory.inventory_service.service.InventoryService;

import java.time.Instant;

@Component
@AllArgsConstructor
@KafkaListener(topics = "inventory-commands", groupId = "inventory-svc")
public class CommandListener {
    private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
    
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafka;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaHandler
    public void handle(ReserveStock cmd) {
        if (wasProcessed(cmd.getEventId())) return;
        try {
            HoldDTO hold = inventoryService.reserve(cmd.getStoreId(), cmd.getItemId(), cmd.getQty(), cmd.getHoldId(), cmd.getTtlUntil());
            if(hold.getStoreId().equals(cmd.getStoreId()) && hold.getItemId().equals(cmd.getItemId())) {
                kafka.send("inventory-events", key(cmd.getStoreId(), cmd.getItemId()),
                        new StockReserved(cmd.getEventId(), cmd.getStoreId(), cmd.getItemId(), cmd.getQty(), cmd.getHoldId(), hold.getExpiresAt()));
                markProcessed(cmd.getEventId());
            } else
                throw new IllegalArgumentException("Hold does not match store and its item");

        } catch (Exception e) {
            logger.error("Reservation error for item {}", cmd.getItemId(), e);
            kafka.send("inventory-commands.dlq", key(cmd.getStoreId(), cmd.getItemId()), cmd);
        }
    }

    @KafkaHandler
    public void handle(ConfirmReservation cmd) {
        if (wasProcessed(cmd.getEventId())) return;
        try{
            HoldDTO hold = inventoryService.findByHoldId(cmd.getHoldId());
            if(hold == null)
                    throw  new IllegalArgumentException("Hold not found");

            if (!"ACTIVE".equals(hold.getStatus()))
                throw new IllegalArgumentException("Hold should be ACTIVE");

            if(hold.getStoreId().equals(cmd.getStoreId()) && hold.getItemId().equals(cmd.getItemId())) {
                inventoryService.confirm(cmd.getHoldId());
                kafka.send("inventory-events", key(cmd.getStoreId(), cmd.getItemId()),
                        new StockDecremented(cmd.getEventId(), cmd.getStoreId(), cmd.getItemId(), hold.getQty(), cmd.getHoldId()));
                markProcessed(cmd.getEventId());
            } else
                throw new IllegalArgumentException("Hold does not match store and its item");

        } catch (Exception e) {
            logger.error("Confirmation error for item {}", cmd.getItemId(), e);
            kafka.send("inventory-commands.dlq", key(cmd.getStoreId(), cmd.getItemId()), cmd);
        }
    }

    @KafkaHandler
    public void handle(ReleaseReservation cmd) {
        if (wasProcessed(cmd.getEventId())) return;
        try{
            HoldDTO hold = inventoryService.findByHoldId(cmd.getHoldId());
            if(hold == null)
                throw new IllegalArgumentException("Hold not found");

            if (!"ACTIVE".equals(hold.getStatus()))
                throw new IllegalArgumentException("Hold should be ACTIVE");

            if(hold.getStoreId().equals(cmd.getStoreId()) && hold.getItemId().equals(cmd.getItemId())) {
                inventoryService.release(cmd.getHoldId());
                kafka.send("inventory-events", key(cmd.getStoreId(), cmd.getItemId()),
                        new StockReleased(cmd.getEventId(), cmd.getStoreId(), cmd.getItemId(), hold.getQty(), cmd.getHoldId()));
                markProcessed(cmd.getEventId());
            }else
                throw new IllegalArgumentException("Hold does not match store and its item");

        } catch (Exception e) {
            logger.error("Release error for item {}", cmd.getItemId(), e);
            kafka.send("inventory-commands.dlq", key(cmd.getStoreId(), cmd.getItemId()), cmd);
        }
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object unknown) {
        logger.error("Unknown command {}", unknown);
    }

    private String key(String storeId, String itemId) {
        return storeId + ":" + itemId;
    }

    private boolean wasProcessed(String eventId) {
        return processedEventRepository.findById(eventId).isPresent();
    }

    private void markProcessed(String eventId) {
        var event = new ProcessedEvent();
        event.setEventId(eventId);
        event.setProcessedAt(Instant.now());
        processedEventRepository.save(event);
    }
}
