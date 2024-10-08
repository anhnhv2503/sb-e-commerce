package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.*;
import com.anhnhvcoder.spring_shopping_cart.repository.OrderItemRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.OrderRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.SizeRepository;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.service.CartService;
import com.anhnhvcoder.spring_shopping_cart.service.OrderService;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Order placeOrder(OrderRequest request) {
        User user = userService.getAuthenticatedUser();
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(request.getTotalPrice());
        order.setUser(user);

        Set<OrderItem> orderItems = createOrderItems(request, order);
        order.setOrderItems(orderItems);


        return orderRepository.save(order);
    }

    private Set<OrderItem> createOrderItems(OrderRequest request, Order order){
        return request.getItems().stream().map((item) -> {
            OrderItem orderItem = new OrderItem();
            Size size = sizeRepository.findById(item.getSizeId()).orElseThrow(()-> new ResourceNotFoundException("Size not found"));
            size.setQuantity(size.getQuantity() - item.getQuantity());
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            orderItem.setSize(size);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(item.getQuantity()).multiply(product.getPrice()));
            orderItem.setOrder(order);
            sizeRepository.save(size);
            return new OrderItem(item.getQuantity(), product.getPrice(), product, order, size);
        }).collect(Collectors.toSet());
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

}
