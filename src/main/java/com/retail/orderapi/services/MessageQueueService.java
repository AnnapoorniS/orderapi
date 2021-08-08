package com.retail.orderapi.services;

import com.retail.orderapi.models.Order;
import com.retail.orderapi.models.PlaceOrderResponse;

public interface MessageQueueService {
    PlaceOrderResponse pushOrderToQueue(Order order);
}
