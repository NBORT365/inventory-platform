package uy.com.inventory.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.inventory.inventory_service.domain.Hold;
import uy.com.inventory.inventory_service.dto.HoldDTO;

import java.util.List;
import java.util.Optional;

public interface HoldRepository extends JpaRepository<Hold, Long> {
    Optional<Hold> findByHoldId(String holdId);
    List<HoldDTO> findByStoreId(String storeId);
}
