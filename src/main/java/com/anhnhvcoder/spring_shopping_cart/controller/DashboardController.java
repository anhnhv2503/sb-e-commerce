package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.response.DashboardResponse;
import com.anhnhvcoder.spring_shopping_cart.service.Impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardData() {
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .data(dashboardService.getDashboardData())
                .build());
    }
}
