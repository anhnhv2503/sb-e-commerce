package com.anhnhvcoder.spring_shopping_cart.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRequest {
    private int code;
    private String id;
    private boolean cancel;
    private String status;
    private long orderCode;
}
