package uy.com.inventory.inventory_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uy.com.inventory.inventory_service.dto.StockDTO;
import uy.com.inventory.inventory_service.service.InventoryService;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryControllerTest {

    private MockMvc mvc;
    private InventoryService inventoryService;

    @BeforeEach
    void setup() {
        inventoryService = Mockito.mock(InventoryService.class);
        InventoryController controller = new InventoryController(inventoryService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /inventory/{storeId}/items/{itemId} → 200 y payload correcto")
    void getItem_ok() throws Exception {
        StockDTO stockDTO = new StockDTO();

        stockDTO.setId(1L);
        stockDTO.setStoreId("S1");
        stockDTO.setItemId("I1");
        stockDTO.setAvailable(10);
        stockDTO.setReserved(0);


        Mockito.when(inventoryService.findByStoreIdAndItemId("S1","I1"))
                .thenReturn(stockDTO);

        mvc.perform(get("/inventory/S1/items/I1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storeId", is("S1")))
                .andExpect(jsonPath("$.itemId", is("I1")))
                .andExpect(jsonPath("$.available", is(10)))
                .andExpect(jsonPath("$.reserved", is(0)));
    }

}
