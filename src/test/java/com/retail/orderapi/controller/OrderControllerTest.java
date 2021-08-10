package com.retail.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.orderapi.models.Order;
import com.retail.orderapi.models.OrderStatus;
import com.retail.orderapi.models.OrderStatusResponse;
import com.retail.orderapi.models.PlaceOrderResponse;
import com.retail.orderapi.repository.MongoRepository;
import com.retail.orderapi.services.MessageQueueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    private MockMvc mvc;

    @InjectMocks
    OrderController orderController;

    @Mock
    MongoRepository mongoRepository;

    @Mock
    MessageQueueService messageQueueService;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws Exception {
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(orderController)
                .build();
    }

    @Test
    public void getOrderStatus() throws Exception{
        //given
        Order order = new Order();
        order.setOrderId("123");
        order.setStatus(OrderStatus.PLACED);
        OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
        orderStatusResponse.setOrderId(order.getOrderId());
        orderStatusResponse.setStatus(order.getStatus());
        BDDMockito.given(mongoRepository.findById("123")).willReturn(java.util.Optional.of(order));

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/v1/order/status/123"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString().equals(mapper.writeValueAsString(orderStatusResponse)));
    }

    @Test
    public void getOrderStatusForOrderWhichIsNotFound() throws Exception{

        //given
        BDDMockito.given(mongoRepository.findById("1234")).willReturn(java.util.Optional.empty());

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/v1/order/status/1234"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void placeOrder() throws Exception{
        //given
        String order = "{\n" +
                "    \"customerUserName\": \"Jack@gmail.com\",\n" +
                "    \"orderDate\":\"21-Sep-2020\",\n" +
                "    \"paymentMode\": \"UPI\",\n" +
                "    \"shippingAddress\": \"No. 8 Bazaar Lane , Mylapore,Chennai - 600 004\",\n" +
                "    \"orderedItems\":[\n" +
                "    { \n" +
                "      \"quantity\" : 1,\n" +
                "      \"productId\" : 156,\n" +
                "      \"productName\" : \"Organic Handwash\",\n" +
                "      \"productPrice\" : 400\n" +
                "      \n" +
                "    },\n" +
                "    { \n" +
                "      \"quantity\" : 2,\n" +
                "      \"productId\" : 137,\n" +
                "      \"productName\" : \"Organic Shampoo\",\n" +
                "      \"productPrice\" : 500\n" +
                "      \n" +
                "    }\n" +
                "    ],\n" +
                "    \"itemsTotalPrice\": 1400,\n" +
                "    \"packingCost\": 100,\n" +
                "    \"tax\": 30,\n" +
                "    \"total\":1530\n" +
                "}";
        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setStatus(OrderStatus.PLACED);
        BDDMockito.given(messageQueueService.pushOrderToQueue(any())).willReturn(placeOrderResponse);

        //when & then
        mvc.perform(MockMvcRequestBuilders.post("/v1/order")
                        .accept(MediaType.ALL)
                        .content(order)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }


    @Test
    public void placeOrderFailed() throws Exception{
        //given
        String order = "{\n" +
                "    \"customerUserName\": \"Jack@gmail.com\",\n" +
                "    \"orderDate\":\"21-Sep-2020\",\n" +
                "    \"paymentMode\": \"UPI\",\n" +
                "    \"shippingAddress\": \"No. 8 Bazaar Lane , Mylapore,Chennai - 600 004\",\n" +
                "    \"orderedItems\":[\n" +
                "    { \n" +
                "      \"quantity\" : 1,\n" +
                "      \"productId\" : 156,\n" +
                "      \"productName\" : \"Organic Handwash\",\n" +
                "      \"productPrice\" : 400\n" +
                "      \n" +
                "    },\n" +
                "    { \n" +
                "      \"quantity\" : 2,\n" +
                "      \"productId\" : 137,\n" +
                "      \"productName\" : \"Organic Shampoo\",\n" +
                "      \"productPrice\" : 500\n" +
                "      \n" +
                "    }\n" +
                "    ],\n" +
                "    \"itemsTotalPrice\": 1400,\n" +
                "    \"packingCost\": 100,\n" +
                "    \"tax\": 30,\n" +
                "    \"total\":1530\n" +
                "}";
        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setStatus(OrderStatus.FAILED);
        BDDMockito.given(messageQueueService.pushOrderToQueue(any())).willReturn(placeOrderResponse);

        //when & then
        mvc.perform(MockMvcRequestBuilders.post("/v1/order")
                        .accept(MediaType.ALL)
                        .content(order)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void placeOrderWithBadRequest() throws Exception{
        //given
        String order = "{\n" +
                "    \"customerUserName\": \"Jack@gmail.com\",\n" +
                "    \"orderDate\":\"21-Sep-2020\",\n" +
                "    \"paymentMode\": \"UPI\",\n" +
                "    \"orderedItems\":[\n" +
                "    { \n" +
                "      \"quantity\" : 1,\n" +
                "      \"productId\" : 156,\n" +
                "      \"productName\" : \"Organic Handwash\",\n" +
                "      \"productPrice\" : 400\n" +
                "      \n" +
                "    },\n" +
                "    { \n" +
                "      \"quantity\" : 2,\n" +
                "      \"productId\" : 137,\n" +
                "      \"productName\" : \"Organic Shampoo\",\n" +
                "      \"productPrice\" : 500\n" +
                "      \n" +
                "    }\n" +
                "    ],\n" +
                "    \"itemsTotalPrice\": 1400,\n" +
                "    \"packingCost\": 100,\n" +
                "    \"tax\": 30,\n" +
                "    \"total\":1530\n" +
                "}";
        Order orderObject = mapper.readValue(order, Order.class);
        PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
        placeOrderResponse.setStatus(OrderStatus.FAILED);
        BDDMockito.given(messageQueueService.pushOrderToQueue(orderObject)).willReturn(placeOrderResponse);

        //when & then
        mvc.perform(MockMvcRequestBuilders.post("/v1/order")
                        .accept(MediaType.ALL)
                        .content(order)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}
