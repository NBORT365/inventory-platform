package uy.com.inventory.inventory_service.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import uy.com.inventory.inventory_service.domain.Hold;
import uy.com.inventory.inventory_service.domain.Stock;
import uy.com.inventory.inventory_service.dto.HoldDTO;
import uy.com.inventory.inventory_service.dto.StockDTO;
import uy.com.inventory.inventory_service.exceptions.InsufficientStockException;
import uy.com.inventory.inventory_service.exceptions.NotFoundException;
import uy.com.inventory.inventory_service.repository.HoldRepository;
import uy.com.inventory.inventory_service.repository.StockRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InventoryService {
    private final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final int RETRY_COUNT = 3;

    private final StockRepository stockRepository;
    private final HoldRepository holdRepository;

    @Transactional
    public HoldDTO reserve(String storeId, String itemId, int qty, String holdId, Instant ttlUntil){
        int retryCount = 0;
        while (true) {
            try {
                if (qty <= 0)
                    throw new IllegalArgumentException("The quantity must be greater than zero");

                Stock stock = stockRepository.findByStoreIdAndItemId(storeId, itemId)
                        .orElseThrow(() -> {
                            log.error("Stock not found {}:{}", storeId, itemId);
                            return new NotFoundException("Stock not found: " + storeId + ":" + itemId);
                        });

                if (stock.getAvailable() < qty){
                    log.error("Stock available less than the required quantity: {}", qty);
                    throw new InsufficientStockException("Insufficient stock");
                }

                stock.setAvailable(stock.getAvailable() - qty);
                stock.setReserved(stock.getReserved() + qty);
                Optional<Hold> byHoldId = holdRepository.findByHoldId(holdId);

                if (byHoldId.isPresent())
                    return new HoldDTO(byHoldId.get());
                else {
                    Hold hold = new Hold();
                    hold.setHoldId(holdId);
                    hold.setStoreId(storeId);
                    hold.setItemId(itemId);
                    hold.setQty(qty);
                    hold.setExpiresAt(ttlUntil);
                    hold.setStatus("ACTIVE");

                    holdRepository.save(hold);

                    return new HoldDTO(hold);
                }
            } catch (ObjectOptimisticLockingFailureException e) {
                if (retryCount == 2)
                    log.error("Failed to reserve stock {}:{}", storeId, itemId);
                else
                    retryCount++;
            }
        }
    }

    @Transactional
    public void confirm(String holdId) {
        Hold hold = holdRepository.findByHoldId(holdId).orElseThrow( () -> new IllegalArgumentException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus()))
            return;

        Stock stock = stockRepository
                .findByStoreIdAndItemId(hold.getStoreId(), hold.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        stock.setReserved(stock.getReserved() - hold.getQty());

        hold.setStatus("CONFIRMED");
    }

    @Transactional
    public void release(String holdId) {
        Hold hold = holdRepository.findByHoldId(holdId).orElseThrow(() -> new IllegalArgumentException("Hold order not found"));

        if (!"ACTIVE".equals(hold.getStatus()))
            return;

        Stock stock = stockRepository
                .findByStoreIdAndItemId(hold.getStoreId(), hold.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        stock.setReserved(stock.getReserved() - hold.getQty());
        stock.setAvailable(stock.getAvailable() + hold.getQty());

        hold.setStatus("RELEASED");
    }

    public StockDTO findByStoreIdAndItemId(String storeId, String itemId) {
        return new StockDTO(stockRepository.findByStoreIdAndItemId(storeId, itemId).get());
    }

    public boolean existsByStoreIdAndItemId(String storeId, String itemId) {
        return stockRepository.existsByStoreIdAndItemId(storeId, itemId);
    }
    public List<StockDTO> findByStore(String storeId) {
        return stockRepository.findByStoreId(storeId);
    }

    public HoldDTO findByHoldId(String holdId){
        Optional<Hold> byHoldId = holdRepository.findByHoldId(holdId);
        return byHoldId.map(HoldDTO::new).orElse(null);
    }

    public List<HoldDTO> findHoldingOrdersByStore(String storeId){
        return holdRepository.findByStoreId(storeId);
    }
}
