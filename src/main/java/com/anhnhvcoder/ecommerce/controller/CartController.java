package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SimpMessagingTemplate messagingTemplate;


    @Operation(summary = "API for get cart", description = "Authenticated User view cart")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponse> getCart(){
        try {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), cartService.getCart()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    @Operation(summary = "API for get cart item", description = "Authenticated User view cart item")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getCartItemQuantity(){
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), cartService.getCartCount()));
    }

}
