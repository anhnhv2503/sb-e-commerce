package com.anhnhvcoder.spring_shopping_cart.mapper;

import com.anhnhvcoder.spring_shopping_cart.dto.ProductImageDTO;
import com.anhnhvcoder.spring_shopping_cart.model.ProductImages;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductImageMapper {

    public List<ProductImageDTO> toProductImageDTO(List<ProductImages> productImages) {

        return productImages.stream().map((image) -> {
            ProductImageDTO productImageDTO = new ProductImageDTO();
            productImageDTO.setId(image.getId());
            productImageDTO.setUrl(image.getUrl());
            return productImageDTO;
        }).toList();
    }
}
