package com.anhnhvcoder.spring_shopping_cart.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequest {
    private BigDecimal totalPrice;
    private String orderAddress;
    private Set<OrderItemRequest> items;

}
