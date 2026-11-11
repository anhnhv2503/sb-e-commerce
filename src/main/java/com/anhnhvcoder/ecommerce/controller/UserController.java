package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.model.User;
import com.anhnhvcoder.ecommerce.request.ResetPasswordRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestParam String fullName,
                                                    @RequestParam String phone,
                                                    @RequestParam String email,
                                                    @RequestParam String password,
                                                    @RequestParam String address) throws MessagingException {

        return ResponseEntity.ok(new ApiResponse(HttpStatus.CREATED.value(), userService.registerUser(fullName, phone, email, password, address)));
    }

    @Operation(description = "Deprecated", summary = "API for seed admin account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestParam String fullName,
                                                    @RequestParam String phone,
                                                    @RequestParam String email,
                                                    @RequestParam String password,
                                                    @RequestParam String address){

        return ResponseEntity.ok(new ApiResponse(HttpStatus.CREATED.value(), userService.registerAdmin(fullName, phone, email, password, address)));
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.getUserById(userId)));
    }

    @Operation(summary = "API for update profile", description = "Authenticated User update profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestParam String fullName,
                                                  @RequestParam String phone,
                                                  @RequestParam String address){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.updateUser(fullName, phone, address)));
    }

    @Operation(summary = "API for change password", description = "Authenticated User change account password")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestParam String oldPassword,
                                                      @RequestParam String newPassword){
        try {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.changePassword(oldPassword, newPassword)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) throws MessagingException {
        try {
            User user = userService.forgotPassword(email);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "An email has been sent to " + email + " to reset your password."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.resetPassword(request)));
    }
}
