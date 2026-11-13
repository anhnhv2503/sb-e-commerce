package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin - Dashboard", description = "Statistics for the admin panel (ROLE_ADMIN only)")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Dashboard summary",
            description = "Totals for orders, products, users, categories, brands, revenue (DELIVERED orders), pending orders, new users this month and order counts by status")
    @GetMapping("/data")
    public ResponseEntity<ApiResponse> getDashboardData() {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getDashboardData()));
    }

    @Operation(summary = "Order count by status")
    @GetMapping("/order-status")
    public ResponseEntity<ApiResponse> getOrderStatusStatistics() {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getOrderStatusStatistics()));
    }

    @Operation(summary = "Monthly revenue", description = "Revenue and order count of DELIVERED orders for each month (1-12) of the given year")
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse> getMonthlyRevenue(
            @Parameter(description = "Year, defaults to the current year")
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getMonthlyRevenue(targetYear)));
    }

    @Operation(summary = "Best-selling products", description = "Products ranked by quantity sold, excluding cancelled orders")
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse> getTopSellingProducts(
            @Parameter(description = "Number of products to return (1-50)")
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getTopSellingProducts(limit)));
    }

    @Operation(summary = "Low-stock sizes", description = "Product sizes whose stock is at or below the threshold")
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse> getLowStockProducts(
            @Parameter(description = "Stock threshold")
            @RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getLowStockProducts(threshold)));
    }
}
