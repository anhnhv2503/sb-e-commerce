package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final OrderService orderService;

    @PostMapping("/payos/create-payment")
    public ResponseEntity<ApiResponse> createPayment(@RequestParam double price) throws Exception {
        return ResponseEntity.ok(new ApiResponse(1000, orderService.createPayOSPayment(price)));
    }
}
