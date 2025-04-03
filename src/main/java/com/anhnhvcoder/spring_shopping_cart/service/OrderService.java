package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.dto.PaymentDTO;
import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.request.PaymentRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Order placeOrder(OrderRequest request);

    List<Order> getOrdersByUserId(OrderStatus status);

    Order cancelOrder(Long orderId);

    Page<Order> getAllOrders(int page, OrderStatus status);

    Order updateOrderStatus(Long orderId);

    Order confirmDelivered(Long orderId);

    PaymentDTO.PayOSResponse createPayOSPayment() throws Exception;

    Order executePaymentOrder(PaymentRequest request);
}
