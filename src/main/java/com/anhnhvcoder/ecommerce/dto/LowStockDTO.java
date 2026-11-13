package com.anhnhvcoder.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LowStockDTO {

    private Long productId;
    private String productName;
    private Long sizeId;
    private String sizeName;
    private int quantity;
}
