package com.retail.orderapi.services.internal;

import com.retail.orderapi.models.Order;
import com.retail.orderapi.models.OrderStatus;
import com.retail.orderapi.models.PlaceOrderResponse;
import com.retail.orderapi.repository.MongoRepository;
import com.retail.orderapi.services.MessageQueueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaService implements MessageQueueService {

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @Autowired
    PlaceOrderResponse placeOrderResponse;

    @Autowired
    MongoRepository mongoRepository;

    private final static Logger logger = LogManager.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Override
    public PlaceOrderResponse pushOrderToQueue(Order order) {

        ListenableFuture<SendResult<String, Order>> future = kafkaTemplate.send(topicName, order);
        placeOrderResponse.setStatus(OrderStatus.PLACED);
        placeOrderResponse.setOrderTrackingId(order.getOrderId());
        placeOrderResponse.setMessage("Successfully placed order for processing");
        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, Order> result) {
                logger.info("Successfully placed order for ID: " + order.getOrderId() + " to processing queue");
                order.setStatus(OrderStatus.PLACED);
                mongoRepository.insert(order);
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                logger.error("Error while placing order with order ID: " + order.getOrderId() + " to processing queue. " + exception);
                order.setStatus(OrderStatus.FAILED);
                mongoRepository.insert(order);
            }
        });

        return placeOrderResponse;

    }
}
