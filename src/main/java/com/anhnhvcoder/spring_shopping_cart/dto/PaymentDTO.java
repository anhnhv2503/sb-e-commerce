package com.anhnhvcoder.spring_shopping_cart.dto;

import lombok.Builder;

public class PaymentDTO {
    @Builder
    public static class VNPayResponse{
        public String code;
        public String message;
        public String paymentUrl;
    }
}
