package com.anhnhvcoder.spring_shopping_cart.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChartResponse {

    private String category;
    private Long quantity;
}
