package com.anhnhvcoder.spring_shopping_cart.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ApiResponse {

    private int status;
    private Object data;
}
