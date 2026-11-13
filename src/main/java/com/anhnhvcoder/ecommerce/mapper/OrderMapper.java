package com.anhnhvcoder.ecommerce.mapper;

import com.anhnhvcoder.ecommerce.dto.OrderDTO;
import com.anhnhvcoder.ecommerce.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;

    public OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
        dto.setPaymentType(order.getPaymentType() != null ? order.getPaymentType().name() : null);
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderAddress(order.getOrderAddress());
        dto.setItems(orderItemMapper.toOrderItemDTOSet(order.getOrderItems()));
        dto.setUser(userMapper.toUserDTO(order.getUser()));
        return dto;
    }
}
