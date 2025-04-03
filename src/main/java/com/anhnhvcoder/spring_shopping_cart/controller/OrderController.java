package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.model.Order;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.request.PaymentRequest;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.Impl.VNPayService;
import com.anhnhvcoder.spring_shopping_cart.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @PostMapping("/vnpay-callback")
    public ResponseEntity<?> vnPayCallback(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest){
        String vnp_ResponseCode = httpServletRequest.getParameter("vnp_ResponseCode");
        if(vnp_ResponseCode.equals("00")){
            return ResponseEntity.ok(new ApiResponse(1000, orderService.placeOrder(request)));
        }else{
            return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment failed"));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse> getOrdersByUserId(@RequestParam OrderStatus status){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.getOrdersByUserId(status)));
    }

    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.cancelOrder(orderId)));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Order>> getAllOrders(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam OrderStatus status){
        return ResponseEntity.ok(orderService.getAllOrders(page, status));
    }

    @PutMapping("/update-order-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.updateOrderStatus(orderId)));
    }

    @PutMapping("/confirm-delivered/{orderId}")
    public ResponseEntity<?> confirmDelivered(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.confirmDelivered(orderId)));
    }

    @PostMapping("/payos/create-link")
    public ResponseEntity<ApiResponse> createPaymentLink() throws Exception {
        return ResponseEntity.ok(new ApiResponse(200, orderService.createPayOSPayment()));
    }

    @PostMapping("/payos/execute")
    public ResponseEntity<ApiResponse> executePayment(@RequestBody PaymentRequest request){
        if(request.getCode() == 00 && !request.isCancel()){
            return ResponseEntity.ok(new ApiResponse(200, orderService.executePaymentOrder(request)));
        }else{
            return ResponseEntity.ok(new ApiResponse(1001, orderService.executePaymentOrder(request)));
        }
    }
}
