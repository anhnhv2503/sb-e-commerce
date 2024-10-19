package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.Impl.VNPayService;
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
    private final VNPayService vnPayService;

    @PostMapping("/place-order")
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequest request){
        if(request.getPaymentMethod().equals("cod")){
            Order order = orderService.placeOrder(request);
            return ResponseEntity.ok(new ApiResponse(1000, order));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment method not supported yet"));
    }

    @PostMapping("/vnpay")
    public ResponseEntity<?> vnPay(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest){
        if(request.getPaymentMethod().equals("vnpay")){
            return ResponseEntity.ok(new ApiResponse(1000, vnPayService.createVNPayPayment(request, httpServletRequest)));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment method not supported yet"));
    }
}
