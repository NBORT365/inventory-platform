package uy.com.inventory.sync_service.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import uy.com.inventory.common.event.StockDecremented;
import uy.com.inventory.common.event.StockReleased;
import uy.com.inventory.common.event.StockReserved;
import uy.com.inventory.common.event.StockSnapshotted;
import uy.com.inventory.sync_service.InventoryClient;
import uy.com.inventory.sync_service.view.State;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@KafkaListener(topics = "inventory-events", groupId = "sync-svc")
public class InventoryEventsHandlers {

    private final Map<String, State> view = new ConcurrentHashMap<>();
    private final InventoryClient inventoryClient;

    @KafkaHandler
    public void on(StockReserved stockReserved) {
        view.compute(key(stockReserved.getStoreId(), stockReserved.getItemId()), (k, s) -> {
            int baseAvail = (s == null)
                    ? inventoryClient.fetchAvailable(stockReserved.getStoreId(), stockReserved.getItemId())
                    : s.getAvailable();
            int avail = baseAvail - stockReserved.getQty();
            int res   = (s == null ? 0 : s.getReserved()) + stockReserved.getQty();
            return new State(stockReserved.getStoreId(), stockReserved.getItemId(), avail, res);
        });
    }

    @KafkaHandler
    public void on(StockDecremented stockDecremented) {
        view.computeIfPresent(key(stockDecremented.getStoreId(), stockDecremented.getItemId()), (k, s) ->
                new State(s.getStoreId(), s.getItemId(), s.getAvailable(), Math.max(0, s.getReserved() - stockDecremented.getQty()))
        );
    }

    @KafkaHandler
    public void on(StockReleased stockReleased) {
        view.computeIfPresent(key(stockReleased.getStoreId(), stockReleased.getItemId()), (k, s) ->
                new State(s.getStoreId(), s.getItemId(), s.getAvailable() + stockReleased.getQty(), Math.max(0, s.getReserved() - Math.max(0, stockReleased.getQty())))
        );
    }

    @KafkaHandler
    public void on(StockSnapshotted e) {
        view.put(key(e.getStoreId(), e.getItemId()),
                new State(e.getStoreId(), e.getItemId(), e.getAvailable(), e.getReserved()));
    }

    // default
    @KafkaHandler(isDefault = true)
    public void onUnknown(Object evt) {
        System.out.println("unknown event in sync-service: " + evt);
    }

    private String key(String storeId, String itemId) { return storeId + ":" + itemId; }

    public State get(String storeId, String itemId) {
        //Este solo es un mapa que refleja los movimientos de stock,
        //en un caso real en un metodo similar ra la logica de sincronizacion del stock
        return view.get(key(storeId, itemId));
    }
}
