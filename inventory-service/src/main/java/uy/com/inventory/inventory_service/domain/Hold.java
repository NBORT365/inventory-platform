package uy.com.inventory.inventory_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

import java.time.Instant;

@Entity
@Table(name="hold", indexes = @Index(name="ix_hold_holdId", columnList="holdId", unique = true))
@NoArgsConstructor(force=true)
@Getter
@Setter
public class Hold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    String holdId;
    String storeId;
    String itemId;
    int qty;
    Instant expiresAt;
    String status; // ACTIVE | CONFIRMED | RELEASED


}