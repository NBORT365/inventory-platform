package uy.com.inventory.inventory_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"storeId","itemId"}))
@NoArgsConstructor
@Getter
@Setter
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    String storeId;
    String itemId;
    int available;
    int reserved;
    @Version long version;
}
