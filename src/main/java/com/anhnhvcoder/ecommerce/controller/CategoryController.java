package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.model.Category;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "API for add new category", description = "Only ROLE_ADMIN can add new category")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestParam("name") String name) {

        Category category = new Category();
        category.setName(name);
        categoryService.addCategory(category);

        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED.value(), category);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), categoryService.getAllCategories());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable("id") Long id) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), categoryService.getCategoryById(id));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API for delete category", description = "Only ROLE_ADMIN can delete category")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), "Category deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
