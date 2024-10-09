package com.anhnhvcoder.spring_shopping_cart.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {

    private String token;
    private String newPassword;
}
