package uy.com.inventory.sync_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Data
@Component
public class InventoryClient {
    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);
    private final RestClient http;

    public InventoryClient(@Value("${inventory.api.base-url}") String baseUrl) {
        if (baseUrl == null || !baseUrl.startsWith("http")) {
            throw new IllegalArgumentException("inventory.api.base-url debe incluir esquema, ej: http://localhost:8082");
        }
        this.http = RestClient.builder().baseUrl(baseUrl).build();
        log.info("InventoryClient baseUrl={}", baseUrl);
    }

    public int fetchAvailable(String storeId, String itemId) {
        var resp = http.get()
                .uri("/inventory/{storeId}/items/{itemId}", storeId, itemId)
                .retrieve()
                .toEntity(InventoryDto.class);
        return resp.getBody() != null ? resp.getBody().available() : 0;
    }

    public record InventoryDto(String storeId, String itemId, int available, int reserved) {}
}
