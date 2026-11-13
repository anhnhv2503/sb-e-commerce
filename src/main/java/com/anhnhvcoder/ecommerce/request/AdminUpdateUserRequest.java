package com.anhnhvcoder.ecommerce.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateUserRequest {

    private String fullName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number. It should contain 10 digits.")
    private String phone;

    private String address;
}
