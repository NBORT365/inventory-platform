package uy.com.inventory.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.inventory.inventory_service.domain.ProcessedEvent;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
