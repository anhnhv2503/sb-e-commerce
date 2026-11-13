package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.dto.OrderDTO;
import com.anhnhvcoder.ecommerce.dto.PaymentDTO;
import com.anhnhvcoder.ecommerce.enums.OrderStatus;
import com.anhnhvcoder.ecommerce.enums.PaymentStatus;
import com.anhnhvcoder.ecommerce.enums.PaymentType;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.mapper.OrderMapper;
import com.anhnhvcoder.ecommerce.model.*;
import com.anhnhvcoder.ecommerce.repository.*;
import com.anhnhvcoder.ecommerce.request.OrderItemRequest;
import com.anhnhvcoder.ecommerce.request.OrderRequest;
import com.anhnhvcoder.ecommerce.request.PaymentRequest;
import com.anhnhvcoder.ecommerce.service.CartService;
import com.anhnhvcoder.ecommerce.service.OrderService;
import com.anhnhvcoder.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        // One query for every size in the order (with its product) instead of two per item.
        Map<Long, Size> sizes = sizeRepository.findByIdIn(
                        request.getItems().stream().map(OrderItemRequest::getSizeId).toList())
                .stream()
                .collect(Collectors.toMap(Size::getId, Function.identity()));

        return request.getItems().stream().map((item) -> {
            Size size = sizes.get(item.getSizeId());
            if (size == null) {
                throw new ResourceNotFoundException("Size not found");
            }
            // Runs inside placeOrder's transaction: the stock change is flushed by dirty checking
            // and the order item is inserted by the cascade from orderRepository.save(order).
            size.setQuantity(size.getQuantity() - item.getQuantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setSize(size);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(item.getQuantity()).multiply(size.getProduct().getPrice()));
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toSet());
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(OrderStatus status) {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderRepository.findByUserIdAndStatus(user.getId(), status);
        return orders.stream().map(orderMapper::toOrderDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        checkOrderOwnerOrAdmin(order);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only PENDING or IN_PROGRESS orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        // Sizes are batch-loaded; the restock is flushed by dirty checking at commit.
        order.getOrderItems().forEach((item) -> {
            Size size = item.getSize();
            size.setQuantity(size.getQuantity() + item.getQuantity());
        });
        return orderRepository.save(order);
    }

    @Override
    public Page<OrderDTO> getAllOrders(int page, OrderStatus status) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 7);
        Page<Order> orders = status == null
                ? orderRepository.findAllByOrderByOrderDateDescIdDesc(pageable)
                : orderRepository.findByStatusOrderByOrderDateDescIdDesc(pageable, status);
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
        checkOrderOwnerOrAdmin(order);
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
        // Cart items were loaded with their size and product (CartItemRepository.findByCartId),
        // so no extra lookups are needed per item.
        return cartItems.stream().map((item) -> {
            Size size = item.getSize();
            size.setQuantity(size.getQuantity() - item.getQuantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setSize(size);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(item.getQuantity()).multiply(size.getProduct().getPrice()));
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toSet());
    }

    @Transactional
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

    private void checkOrderOwnerOrAdmin(Order order) {
        User user = userService.getAuthenticatedUser();
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleName()));
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to modify this order");
        }
    }

    @Override
    public List<OrderDTO> getNewestOrders() {
        List<Order> orders = orderRepository.findTop3ByOrderByOrderDateDescIdDesc();
        return orders.stream().map(orderMapper::toOrderDTO).collect(Collectors.toList());
    }
}
