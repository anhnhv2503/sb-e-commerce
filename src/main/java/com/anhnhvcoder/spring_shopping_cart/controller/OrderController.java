package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("userId") Long userId){
        try {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED.value(), orderService.placeOrder(userId));
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
