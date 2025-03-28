package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Order placeOrder(OrderRequest request);

    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(OrderStatus status);

    Order cancelOrder(Long orderId);

    Page<Order> getAllOrders(int page, OrderStatus status);

    Order updateOrderStatus(Long orderId);

    Order confirmDelivered(Long orderId);

    String createPayOSPayment(double price) throws Exception;
}
