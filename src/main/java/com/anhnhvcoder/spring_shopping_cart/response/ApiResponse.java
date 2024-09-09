package com.anhnhvcoder.spring_shopping_cart.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {

    private int status;
    private Object data;
}
