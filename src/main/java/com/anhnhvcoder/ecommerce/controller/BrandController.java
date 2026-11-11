package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.model.Brand;
import com.anhnhvcoder.ecommerce.request.BrandRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;


    @Operation(summary = "API create brand", description = "Only ROLE_ADMIN can add new brand")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createBrand(@RequestBody BrandRequest request) {
        Brand createdBrand = brandService.createBrand(request);
        return ResponseEntity.created(null).body(new ApiResponse(HttpStatus.CREATED.value(), createdBrand));
    }

}
