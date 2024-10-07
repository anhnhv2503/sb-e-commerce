package com.anhnhvcoder.spring_shopping_cart.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemRequest {

    private Long productId;
    private Long sizeId;
    private int quantity;
}
