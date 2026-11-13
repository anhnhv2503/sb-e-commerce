package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.model.Brand;
import com.anhnhvcoder.ecommerce.request.BrandRequest;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Product brands")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "API create brand", description = "Only ROLE_ADMIN can add new brand")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createBrand(@RequestBody BrandRequest request) {
        Brand createdBrand = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), createdBrand));
    }

    @Operation(summary = "API get all brands")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBrands() {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), brandService.getAllBrands()));
    }

    @Operation(summary = "API get brand by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBrandById(@PathVariable int id) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), brandService.getBrandById(id)));
    }

    @Operation(summary = "API update brand", description = "Only ROLE_ADMIN can update brand; null fields are left unchanged")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBrand(@PathVariable int id, @RequestBody BrandRequest request) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), brandService.updateBrand(id, request)));
    }

    @Operation(summary = "API delete brand", description = "Only ROLE_ADMIN can delete brand; brands used by products cannot be deleted")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBrand(@PathVariable int id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Brand deleted successfully"));
    }
}
