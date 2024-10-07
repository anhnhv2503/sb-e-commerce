package com.anhnhvcoder.spring_shopping_cart.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequest {
    private BigDecimal totalPrice;
    private List<OrderItemRequest> items;

}
