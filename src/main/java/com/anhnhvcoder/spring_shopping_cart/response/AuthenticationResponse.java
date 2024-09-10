package com.anhnhvcoder.spring_shopping_cart.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private boolean valid;
    private String token;
    private String message;

}
