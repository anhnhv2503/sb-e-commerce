package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequest request){
        Order order = orderService.placeOrder(request);

        return ResponseEntity.ok(new ApiResponse(1000, order));
    }

    @PostMapping("/vnpay")
    public ResponseEntity<?> vnPay(HttpServletRequest request){
        String bankCode = request.getParameter("bank_code");
        String amount = request.getParameter("amount");
        log.info("Bank code: " + bankCode);
        log.info("Amount: " + amount);
        return null;
    }
}
