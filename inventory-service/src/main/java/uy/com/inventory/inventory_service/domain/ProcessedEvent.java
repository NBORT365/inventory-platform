package uy.com.inventory.inventory_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name="processed_event")
@Data
public class ProcessedEvent {
    @Id
    String eventId;
    Instant processedAt;
}
