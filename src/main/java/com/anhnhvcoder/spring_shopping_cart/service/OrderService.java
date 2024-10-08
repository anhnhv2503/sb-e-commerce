package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;

import java.util.List;

public interface OrderService {

    Order placeOrder(OrderRequest request);

    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(Long userId);
}
