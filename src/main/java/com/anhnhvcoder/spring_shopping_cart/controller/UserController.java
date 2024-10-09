package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestParam String fullName,
                                                  @RequestParam String phone,
                                                  @RequestParam String address){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.updateUser(fullName, phone, address)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestParam String oldPassword,
                                                      @RequestParam String newPassword){
        try {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), userService.changePassword(oldPassword, newPassword)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
