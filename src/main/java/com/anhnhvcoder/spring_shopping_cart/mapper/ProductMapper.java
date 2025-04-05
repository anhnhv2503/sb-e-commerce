package com.anhnhvcoder.spring_shopping_cart.mapper;

import com.anhnhvcoder.spring_shopping_cart.dto.ProductDTO;
import com.anhnhvcoder.spring_shopping_cart.dto.ProductImageDTO;
import com.anhnhvcoder.spring_shopping_cart.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductImageMapper productImageMapper;

    public ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setBrand(product.getBrand());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setImages(productImageMapper.toProductImageDTO(product.getImages()));
        return productDTO;
    }
}
