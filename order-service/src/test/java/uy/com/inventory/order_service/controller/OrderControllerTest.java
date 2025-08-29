//package uy.com.inventory.order_service.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import org.springframework.http.MediaType;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import uy.com.inventory.common.command.ReserveStock;
//
//import static org.hamcrest.Matchers.not;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.hamcrest.Matchers.isEmptyOrNullString;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class OrderControllerTest {
//
//    private MockMvc mvc;
//    private KafkaTemplate<String, Object> kafka;
//
//    @BeforeEach
//    void setup() {
//        kafka = Mockito.mock(KafkaTemplate.class);
//        OrderController controller = new OrderController(kafka);
//        mvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }
//
//    @Test
//    @DisplayName("POST /order/{store}/items/{item}/reserve → 201 y envía ReserveStock al tópico")
//    void reserve_ok() throws Exception {
//        String body = "{ \"qty\": 3 }";
//
//        mvc.perform(post("/order/S1/items/I1/reserve")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.holdId", not(isEmptyOrNullString())))
//                .andExpect(jsonPath("$.ttlUntil", notNullValue()));
//
//        ArgumentCaptor<String> topicC = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> keyC = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Object> valueC = ArgumentCaptor.forClass(Object.class);
//
//        Mockito.verify(kafka).send(topicC.capture(), keyC.capture(), valueC.capture());
//        assertEquals("inventory-commands", topicC.getValue());
//        assertEquals("S1:I1", keyC.getValue());
//        assertInstanceOf(ReserveStock.class, valueC.getValue());
//        assertEquals(3, ((ReserveStock) valueC.getValue()).getQty());
//    }
//}