package com.anhnhvcoder.spring_shopping_cart.mapper;

import com.anhnhvcoder.spring_shopping_cart.dto.OrderDTO;
import com.anhnhvcoder.spring_shopping_cart.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getStatus().name());
        dto.setPaymentStatus(order.getPaymentStatus().name());
        dto.setPaymentType(order.getPaymentType().name());
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderAddress(order.getOrderAddress());
        dto.setItems(orderItemMapper.toOrderItemDTOSet(order.getOrderItems()));

        return dto;
    }
}
