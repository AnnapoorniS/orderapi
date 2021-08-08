package com.retail.orderapi.repository;

import com.retail.orderapi.models.Order;

public interface MongoRepository extends org.springframework.data.mongodb.repository.MongoRepository<Order, String> {
}
