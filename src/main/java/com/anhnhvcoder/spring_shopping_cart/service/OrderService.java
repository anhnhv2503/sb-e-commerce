package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(Long userId);

    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(Long userId);
}
