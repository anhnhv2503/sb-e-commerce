package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.dto.OrderDTO;
import com.anhnhvcoder.spring_shopping_cart.dto.PaymentDTO;
import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.enums.PaymentStatus;
import com.anhnhvcoder.spring_shopping_cart.enums.PaymentType;
import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.mapper.OrderMapper;
import com.anhnhvcoder.spring_shopping_cart.model.*;
import com.anhnhvcoder.spring_shopping_cart.repository.*;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.request.PaymentRequest;
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
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
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
    private final CartRepository cartRepository;
    private final PayOS payOS;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

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
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(item.getQuantity()).multiply(product.getPrice()));
            orderItem.setOrder(order);
            sizeRepository.save(size);
            return orderItemRepository.save(orderItem);
        }).collect(Collectors.toSet());
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(OrderStatus status) {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderRepository.findByUserIdAndStatus(user.getId(), status);
        return orders.stream().map(orderMapper::toOrderDTO).collect(Collectors.toList());
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
    public Page<OrderDTO> getAllOrders(int page, OrderStatus status) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(pageable, status);
        return orders.map(orderMapper::toOrderDTO);
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

    @Override
    public PaymentDTO.PayOSResponse createPayOSPayment() throws Exception {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        String currentTimeString = String.valueOf(new Date().getTime());
        long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
        String product = "VA Shop";
        String desc = "Thanh toan " + user.getId() + "-" + orderCode;
        String returnUrl = "http://localhost:5173/payos/callback";
        String cancelUrl = "http://localhost:5173/payos/callback";

        ItemData item = ItemData.builder()
                .name(product)
                .quantity(cart.getTotalItems())
                .price(Integer.valueOf(cart.getTotalPrice().intValue()))
                .build();
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .description(desc)
                .amount(Integer.valueOf(cart.getTotalPrice().intValue()))
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();
        CheckoutResponseData data = payOS.createPaymentLink(paymentData);

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentType(PaymentType.PAYOS);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderCode(orderCode);
        order.setOrderAddress(user.getAddress());
        order.setUser(user);

        orderRepository.save(order);

        return PaymentDTO.PayOSResponse.builder()
                .code("OK")
                .message("PayOS Link")
                .paymentUrl(data.getCheckoutUrl())
                .build();
    }

    private Set<OrderItem> createOrderItem(List<CartItem> cartItems, Order order) {
        return cartItems.stream().map((item) -> {

            OrderItem orderItem = new OrderItem();
            Size size = sizeRepository.findById(item.getSize().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Size not found"));
            size.setQuantity(size.getQuantity() - item.getQuantity());
            Product product = productRepository.findById(item.getSize().getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            orderItem.setSize(size);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(item.getQuantity()).multiply(product.getPrice()));
            orderItem.setOrder(order);
            sizeRepository.save(size);
            return orderItemRepository.save(orderItem);
        }).collect(Collectors.toSet());
    }

    @Override
    public Order executePaymentOrder(PaymentRequest request) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(!request.isCancel()){
            order.setStatus(OrderStatus.IN_PROGRESS);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            Set<OrderItem> orderItems = createOrderItem(items, order);
            order.getOrderItems().clear();
            order.getOrderItems().addAll(orderItems);
            cartService.clearCart();
        }else{
            order.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.CANCELED);
        }
        return orderRepository.save(order);
    }
}
