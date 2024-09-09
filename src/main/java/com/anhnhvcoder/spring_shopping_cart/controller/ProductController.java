package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.model.Product;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestParam("name") String name,
                                                  @RequestParam("brand") String brand,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("price") BigDecimal price,
                                                  @RequestParam("inventory") int inventory,
                                                  @RequestParam("categoryId") Long categoryId,
                                                  @RequestParam("images") MultipartFile[] images) throws IOException {
        Product product = productService.addProduct(name, brand, description, price, inventory, categoryId, images);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getProducts(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getAllProducts());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), "Product deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("brand") String brand,
                                                     @RequestParam("description") String description,
                                                     @RequestParam("price") BigDecimal price,
                                                     @RequestParam("inventory") int inventory,
                                                     @RequestParam("categoryId") Long categoryId
                                                     ) throws IOException {
        Product product = productService.updateProduct(id, name, brand, description, price, inventory, categoryId);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam("brand") String brand){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.findByBrand(brand));
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse> getProductsByCategory(@RequestParam("categoryId") Long categoryId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.findByCategory(categoryId));
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countProduct(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.countProducts());
        return ResponseEntity.ok(apiResponse);
    }
}
