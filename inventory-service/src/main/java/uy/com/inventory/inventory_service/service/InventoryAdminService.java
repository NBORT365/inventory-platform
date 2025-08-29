package uy.com.inventory.inventory_service.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import uy.com.inventory.common.command.InitializeStock;
import uy.com.inventory.common.event.StockSnapshotted;
import uy.com.inventory.inventory_service.domain.Stock;
import uy.com.inventory.inventory_service.repository.StockRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InventoryAdminService {
    private final StockRepository stocks;
    private final KafkaTemplate<String,Object> kafka;

    public boolean existsStockById(Long id) {
        return stocks.existsById(id);
    }

    @Transactional
    public void createSKU(InitializeStock cmd) {
        if (cmd.getAvailable() < 0)
            throw new IllegalArgumentException("Available must be >= 0");

        Stock stock = stocks.findByStoreIdAndItemId(cmd.getStoreId(), cmd.getItemId())
                .orElseGet(Stock::new);

        stock.setStoreId(cmd.getStoreId());;
        stock.setItemId(cmd.getItemId());
        stock.setAvailable(cmd.getAvailable());
        stock.setReserved(0);
        stocks.save(stock);

        kafka.send("inventory-events", key(cmd.getStoreId(), cmd.getItemId()),
                new StockSnapshotted(cmd.getEventId(), cmd.getStoreId(), cmd.getItemId(), stock.getAvailable(), stock.getReserved()));
    }

    @Transactional
    public void deleteByStoreIdAndItemId(String storeId, String itemId) {
        stocks.deleteByStoreIdAndItemId(storeId,itemId);
        //Es necesario actualizar colas
    }

    private String key(String storeId, String itemId) { return storeId + ":" + itemId; }
}
