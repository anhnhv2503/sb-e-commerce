package com.anhnhvcoder.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private boolean active;
    private boolean locked;
    private LocalDate createdAt;
}
