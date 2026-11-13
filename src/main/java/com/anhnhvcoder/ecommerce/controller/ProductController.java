package com.anhnhvcoder.ecommerce.controller;

import com.anhnhvcoder.ecommerce.model.Product;
import com.anhnhvcoder.ecommerce.model.Size;
import com.anhnhvcoder.ecommerce.response.ApiResponse;
import com.anhnhvcoder.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Products", description = "Product catalog, sizes and inventory")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "API add product", description = "Only ROLE_ADMIN. Multipart form with images")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestParam("name") String name,
                                                  @RequestParam("brand") String brand,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("price") BigDecimal price,
                                                  @RequestParam("inventory") int inventory,
                                                  @RequestParam("categoryName") String categoryName,
                                                  @RequestParam("sizeName") String sizeName,
                                                  @RequestParam("images") MultipartFile[] images) throws IOException {
        Product product = productService.addProduct(name, brand, description, price, inventory, categoryName, sizeName, images);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API add size to product", description = "Only ROLE_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/size/add/{productId}")
    public ResponseEntity<ApiResponse> addMoreSize(@PathVariable("productId") Long productId,
                                                   @RequestParam("sizeName") String sizeName,
                                                   @RequestParam("quantity") int quantity){

        Size size = productService.addMoreSize(productId, sizeName, quantity);

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), size));
    }

    @Operation(summary = "API get all products")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getProducts(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getAllProducts());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get product by id")
    @GetMapping("/{id}/get")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), product);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API delete product", description = "Only ROLE_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), "Product deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API update product", description = "Only ROLE_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
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

    @Operation(summary = "API get products by category")
    @GetMapping("/category")
    public ResponseEntity<ApiResponse> getProductsByCategory(@RequestParam("categoryId") Long categoryId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.findByCategory(categoryId));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API count products")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countProduct(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.countProducts());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get new arrival products")
    @GetMapping("/new-arrival")
    public ResponseEntity<ApiResponse> getNewestProducts(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getNewArrivalProducts());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API search products by name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProductByName(@RequestParam(value = "name", required = false) String name){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getProductByName(name));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get all brands")
    @GetMapping("/brand/all")
    public ResponseEntity<ApiResponse> getAllProductBrand(){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getAllBrand());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get inventory of a size")
    @GetMapping("/size/inventory/{sizeId}")
    public ResponseEntity<?> getSizeInventory(@PathVariable("sizeId") Long sizeId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getInventoryBySizeId(sizeId));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get product by size id")
    @GetMapping("/size/{sizeId}")
    public ResponseEntity<?> getProductBySizeId(@PathVariable("sizeId") Long sizeId){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.getProductBySizeId(sizeId));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API edit size inventory", description = "Only ROLE_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/size/edit/{sizeId}")
    public ResponseEntity<?> editSizeInventory(@PathVariable("sizeId") Long sizeId, @RequestParam("quantity") int quantity){
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), productService.editSizeInventory(sizeId, quantity));
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "API get products by page", description = "Filter by category name, brand name and keyword")
    @GetMapping
    public ResponseEntity<Page<Product>> getProductsByPage(
            @RequestParam(name = "categoryName", required = false) String categoryName,
            @RequestParam(name = "brandName", required = false) String brandName,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByPage(categoryName, brandName, search, pageable));
    }

}
