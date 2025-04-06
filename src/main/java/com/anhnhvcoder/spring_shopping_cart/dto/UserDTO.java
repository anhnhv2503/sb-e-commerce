package com.anhnhvcoder.spring_shopping_cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private String role;
}
