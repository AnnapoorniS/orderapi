package com.retail.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.retail.orderapi.models.Order;
import com.retail.orderapi.models.OrderStatus;
import com.retail.orderapi.models.PlaceOrderResponse;
import com.retail.orderapi.repository.MongoRepository;
import com.retail.orderapi.services.MessageQueueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("v1/order")
public class OrderController {

    @Autowired
    MessageQueueService messageQueueService;

    @Autowired
    MongoRepository mongoRepository;

    @Autowired
    ObjectMapper objectMapper;
    private final static Logger logger = LogManager.getLogger(OrderController.class);

    @PostMapping
    @Validated
    ResponseEntity<PlaceOrderResponse> placeOrder(@Valid @RequestBody Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PLACED);
        logger.info("Processing order id: " + order.getOrderId() + " for user : " + order.getCustomerUserName());
        PlaceOrderResponse placeOrderResponse = messageQueueService.pushOrderToQueue(order);
        if (placeOrderResponse != null && placeOrderResponse.getStatus() == OrderStatus.PLACED) {
            return new ResponseEntity<>(placeOrderResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(placeOrderResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("status/{orderId}")
    ResponseEntity<ObjectNode> getOrderStatus(@PathVariable String orderId) {
        Optional<Order> order = mongoRepository.findById(orderId);
        if (order.isPresent()) {
            ObjectNode orderStatusResponse = objectMapper.createObjectNode();
            orderStatusResponse.put("orderId", order.get().getOrderId());
            orderStatusResponse.put("status", String.valueOf(order.get().getStatus()));
            return new ResponseEntity<>(orderStatusResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
