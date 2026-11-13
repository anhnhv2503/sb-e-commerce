package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.dto.OrderDTO;
import com.anhnhvcoder.ecommerce.enums.OrderStatus;
import com.anhnhvcoder.ecommerce.model.Order;
import com.anhnhvcoder.ecommerce.request.OrderRequest;
import com.anhnhvcoder.ecommerce.request.PaymentRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.Impl.VNPayService;
import com.anhnhvcoder.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order placement, payment and order management")
public class OrderController {

    private final OrderService orderService;
    private final VNPayService vnPayService;

    @Operation(summary = "Place order (cash on delivery)", description = "paymentMethod must be 'cod'")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/place-order")
    public ResponseEntity<ApiResponse> placeOrder(@RequestBody OrderRequest request){
        if(request.getPaymentMethod().equals("cod")){
            Order order = orderService.placeOrder(request);
            return ResponseEntity.ok(new ApiResponse(1000, order));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment method not supported yet"));
    }

    @Operation(summary = "Create VNPAY payment URL", description = "paymentMethod must be 'vnpay'")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/vnpay")
    public ResponseEntity<?> vnPay(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest){
        if(request.getPaymentMethod().equals("vnpay")){
            return ResponseEntity.ok(new ApiResponse(1000, vnPayService.createVNPayPayment(request, httpServletRequest)));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment method not supported yet"));
    }

    @Operation(summary = "VNPAY callback", description = "Creates the order when vnp_ResponseCode is 00")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/vnpay-callback")
    public ResponseEntity<?> vnPayCallback(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest){
        String vnp_ResponseCode = httpServletRequest.getParameter("vnp_ResponseCode");
        if(vnp_ResponseCode.equals("00")){
            return ResponseEntity.ok(new ApiResponse(1000, orderService.placeOrder(request)));
        }else{
            return ResponseEntity.badRequest().body(new ApiResponse(1001, "Payment failed"));
        }
    }

    @Operation(summary = "Get my orders by status")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse> getOrdersByUserId(@RequestParam OrderStatus status){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.getOrdersByUserId(status)));
    }

    @Operation(summary = "Cancel order", description = "Owner or admin; only PENDING or IN_PROGRESS orders. Restocks the sizes")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.cancelOrder(orderId)));
    }

    @Operation(summary = "Get all orders", description = "Only ROLE_ADMIN. Paged (7 per page), optional status filter")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(required = false) OrderStatus status){
        return ResponseEntity.ok(orderService.getAllOrders(page, status));
    }

    @Operation(summary = "Get 3 newest orders", description = "Only ROLE_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/newest")
    public ResponseEntity<List<OrderDTO>> getNewestOrders(){
        return ResponseEntity.ok(orderService.getNewestOrders());
    }

    @Operation(summary = "Advance order status", description = "Only ROLE_ADMIN. PENDING -> IN_PROGRESS -> SHIPPING")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update-order-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.updateOrderStatus(orderId)));
    }

    @Operation(summary = "Confirm order delivered", description = "Owner or admin; SHIPPING -> DELIVERED")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/confirm-delivered/{orderId}")
    public ResponseEntity<?> confirmDelivered(@PathVariable Long orderId){
        return ResponseEntity.ok(new ApiResponse(1000, orderService.confirmDelivered(orderId)));
    }

    @Operation(summary = "Create PayOS payment link", description = "Creates a PENDING order from the current cart")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/payos/create-link")
    public ResponseEntity<ApiResponse> createPaymentLink() throws Exception {
        return ResponseEntity.ok(new ApiResponse(200, orderService.createPayOSPayment()));
    }

    @Operation(summary = "Execute PayOS payment", description = "Finalises or cancels the PayOS order")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/payos/execute")
    public ResponseEntity<ApiResponse> executePayment(@RequestBody PaymentRequest request){
        if(request.getCode() == 00 && !request.isCancel()){
            return ResponseEntity.ok(new ApiResponse(200, orderService.executePaymentOrder(request)));
        }else{
            return ResponseEntity.ok(new ApiResponse(1001, orderService.executePaymentOrder(request)));
        }
    }
}
