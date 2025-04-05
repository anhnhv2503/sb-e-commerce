package com.anhnhvcoder.spring_shopping_cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SizeDTO {
    private Long id;
    private String sizeName;
    private int quantity;
    private ProductDTO product;
}
