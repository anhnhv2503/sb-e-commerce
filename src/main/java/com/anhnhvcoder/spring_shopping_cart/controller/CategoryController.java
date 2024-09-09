package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.model.Category;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), "Category deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
