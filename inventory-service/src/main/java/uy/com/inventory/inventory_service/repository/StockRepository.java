package uy.com.inventory.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.inventory.inventory_service.domain.Stock;
import uy.com.inventory.inventory_service.dto.StockDTO;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStoreIdAndItemId(String storeId, String itemId);
    List<StockDTO> findByStoreId(String storeId);
    boolean existsByStoreIdAndItemId(String storeId, String itemId);
    void deleteByStoreIdAndItemId(String storeId, String itemId);
}
