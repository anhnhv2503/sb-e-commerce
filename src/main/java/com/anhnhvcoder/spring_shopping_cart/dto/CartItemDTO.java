package com.anhnhvcoder.spring_shopping_cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemDTO {

    private Long itemId;
    private Long sizeId;
    private Long cartId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private int quantity;
    private String imageUrl;
    private String sizeName;
    private String description;

}
