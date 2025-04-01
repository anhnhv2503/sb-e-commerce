package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Cart;
import com.anhnhvcoder.spring_shopping_cart.repository.CartItemRepository;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponse> getCart(){
        try {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), cartService.getCart()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    @GetMapping("/detail/{userId}")
    public ResponseEntity<ApiResponse> getCartByUserId(@PathVariable("userId") Long userId){
        try {
            Cart cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), cart));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{cartId}/clear-cart")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable("cartId") Long cartId){
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Cart is cleared"));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }

    }

    @GetMapping("/{cartId}/total-amount")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable("cartId") Long cartId){
        try {
            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), totalPrice));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

}
