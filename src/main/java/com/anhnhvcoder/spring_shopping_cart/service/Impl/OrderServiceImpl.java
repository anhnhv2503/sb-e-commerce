package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.enums.PaymentType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        order.setOrderAddress(request.getOrderAddress());
        if(request.getPaymentMethod().equals("vnpay")){
            order.setPaymentType(PaymentType.VNPAY);
        }else if(request.getPaymentMethod().equals("cod")) {
            order.setPaymentType(PaymentType.CASH_ON_DELIVERY);
        }else {
            throw new ResourceNotFoundException("Payment method not supported yet");
        }
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

    @Override
    public List<Order> getOrdersByUserId(OrderStatus status) {
        User user = userService.getAuthenticatedUser();
        return orderRepository.findByUserIdAndStatus(user.getId(), status);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        order.getOrderItems().stream().map((item) -> {
            Size size = item.getSize();
            size.setQuantity(size.getQuantity() + item.getQuantity());
            sizeRepository.save(size);
            return item;
        }).collect(Collectors.toSet());
        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getAllOrders(int page, OrderStatus status) {
        Pageable pageable = PageRequest.of(page, 7);
        return orderRepository.findByStatusOrderByOrderDateDesc(pageable, status);
    }

    @Override
    public Order updateOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(order.getStatus().equals(OrderStatus.PENDING)) {
            order.setStatus(OrderStatus.IN_PROGRESS);
        }else if(order.getStatus().equals(OrderStatus.IN_PROGRESS)){
            order.setStatus(OrderStatus.SHIPPING);
        }else{
            throw new ResourceNotFoundException("Order status can not be updated");
        }
        return orderRepository.save(order);
    }

    @Override
    public Order confirmDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(order.getStatus().equals(OrderStatus.SHIPPING)){
            order.setStatus(OrderStatus.DELIVERED);
            return orderRepository.save(order);
        }
        throw new ResourceNotFoundException("Order status can not be updated");
    }


}
