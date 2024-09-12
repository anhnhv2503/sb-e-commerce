package com.anhnhvcoder.spring_shopping_cart.service;

import com.anhnhvcoder.spring_shopping_cart.model.Product;
import com.anhnhvcoder.spring_shopping_cart.model.ProductImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product addProduct(String name,
                       String brand,
                       String description,
                       BigDecimal price,
                       int inventory,
                       Long categoryId,
                       Long sizeId,
                       MultipartFile[] images) throws IOException;

    List<Product> getAllProducts();

    Product getProductById(Long id);

    void deleteProduct(Long id);

    Product updateProduct(Long id,
                          String name,
                          String brand,
                          String description,
                          BigDecimal price,
                          int inventory,
                          Long categoryId,
                          Long sizeId
    ) throws IOException;

    List<Product> findByBrand(String brand);

    List<Product> findByCategory(Long categoryId);

    Long countProducts();

}
