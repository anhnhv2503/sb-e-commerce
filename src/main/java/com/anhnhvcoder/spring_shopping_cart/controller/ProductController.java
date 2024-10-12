package com.anhnhvcoder.spring_shopping_cart.controller;

import com.anhnhvcoder.spring_shopping_cart.model.Product;
import com.anhnhvcoder.spring_shopping_cart.model.Size;
import com.anhnhvcoder.spring_shopping_cart.response.ApiResponse;
import com.anhnhvcoder.spring_shopping_cart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestParam("name") String name,
                                                  @RequestParam("brand") String brand,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("price") BigDecimal price,
                                                  @RequestParam("inventory") int inventory,
                                                  @RequestParam("categoryId") Long categoryId,
                                                  @RequestParam("sizeName") String sizeName,
                                                  @RequestParam("images") MultipartFile[] images) throws IOException {
        Product product = productService.addProduct(name, brand, description, price, inventory, categoryId, sizeName, images);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/size/add/{productId}")
    public ResponseEntity<ApiResponse> addMoreSize(@PathVariable("productId") Long productId,
                                                   @RequestParam("sizeName") String sizeName,
                                                   @RequestParam("quantity") int quantity){

        Size size = productService.addMoreSize(productId, sizeName, quantity);

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), size));
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), "Product deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("brand") String brand,
                                                     @RequestParam("description") String description,
                                                     @RequestParam("price") BigDecimal price,
                                                     @RequestParam("inventory") int inventory,
                                                     @RequestParam("categoryId") Long categoryId,
                                                     @RequestParam("sizeId") Long sizeId
                                                     ) throws IOException {
        Product product = productService.updateProduct(id, name, brand, description, price, inventory, categoryId, sizeId);
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

    @GetMapping("/new-arrival")
    public ResponseEntity<ApiResponse> getNewestProducts(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getNewArrivalProducts());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProductByName(@RequestParam(value = "name", required = false) String name){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getProductByName(name));
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/brand/all")
    public ResponseEntity<ApiResponse> getAllProductBrand(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getAllDistinctBrand());
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/size/inventory/{sizeId}")
    public ResponseEntity<?> getSizeInventory(@PathVariable("sizeId") Long sizeId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getInventoryBySizeId(sizeId));
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/size/{sizeId}")
    public ResponseEntity<?> getProductBySizeId(@PathVariable("sizeId") Long sizeId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getProductBySizeId(sizeId));
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/size/edit/{sizeId}")
    public ResponseEntity<?> editSizeInventory(@PathVariable("sizeId") Long sizeId, @RequestParam("quantity") int quantity){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.editSizeInventory(sizeId, quantity));
        return ResponseEntity.ok(apiResponse);
    }

}
