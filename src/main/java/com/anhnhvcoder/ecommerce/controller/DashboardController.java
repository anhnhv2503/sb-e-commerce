package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/data")
    public ResponseEntity<ApiResponse> getDashboardData() {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), dashboardService.getDashboardData()));
    }

}
