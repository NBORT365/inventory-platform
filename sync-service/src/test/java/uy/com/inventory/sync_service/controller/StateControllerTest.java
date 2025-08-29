package uy.com.inventory.sync_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uy.com.inventory.sync_service.kafka.InventoryEventsHandlers;
import uy.com.inventory.sync_service.view.State;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StateControllerTest {

    private MockMvc mvc;
    private InventoryEventsHandlers consumer;

    @BeforeEach
    void setup() {
        consumer = Mockito.mock(InventoryEventsHandlers.class);
        StateController controller = new StateController(consumer);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /state/{storeId}/{itemId} → 200 con 'no data yet' si no hay estado")
    void get_noDataYet() throws Exception {
        Mockito.when(consumer.get("S1","I1")).thenReturn(null);

        mvc.perform(get("/state/S1/I1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("no data yet")))
                .andExpect(jsonPath("$.storeId", is("S1")))
                .andExpect(jsonPath("$.itemId", is("I1")));
    }

    @Test
    @DisplayName("GET /state/{storeId}/{itemId} → 200 con estado presente")
    void get_withState() throws Exception {
        Mockito.when(consumer.get("S1","I1")).thenReturn(new State("S1","I1", 10, 2));

        mvc.perform(get("/state/S1/I1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId", is("S1")))
                .andExpect(jsonPath("$.itemId", is("I1")))
                .andExpect(jsonPath("$.available", is(10)))
                .andExpect(jsonPath("$.reserved", is(2)));
    }
}
