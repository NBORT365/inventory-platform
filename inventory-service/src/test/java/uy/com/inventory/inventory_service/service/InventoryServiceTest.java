package uy.com.inventory.inventory_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uy.com.inventory.common.command.InitializeStock;
import uy.com.inventory.inventory_service.controller.InventoryAdminController;
import uy.com.inventory.inventory_service.dto.StockDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceTest {

    @Autowired
    private InventoryAdminService admin;

    @Autowired
    private InventoryService inventoryService;

    @Test
    @DisplayName("Inicializa un SKU y luego lo obtiene por storeId+itemId")
    void initializeAndFetch() {
        admin.createSKU(new InitializeStock(UUID.randomUUID().toString(),"S1","I1", 25));
        StockDTO dto = inventoryService.findByStoreIdAndItemId("S1","I1");
        assertNotNull(dto);
        assertEquals("S1", dto.getStoreId());
        assertEquals("I1", dto.getItemId());
        assertEquals(25, dto.getAvailable());
        assertEquals(0, dto.getReserved());
    }
}
