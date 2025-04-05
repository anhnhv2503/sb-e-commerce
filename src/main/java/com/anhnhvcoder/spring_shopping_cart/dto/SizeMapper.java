package com.anhnhvcoder.spring_shopping_cart.dto;

import com.anhnhvcoder.spring_shopping_cart.mapper.ProductImageMapper;
import com.anhnhvcoder.spring_shopping_cart.mapper.ProductMapper;
import com.anhnhvcoder.spring_shopping_cart.model.Product;
import com.anhnhvcoder.spring_shopping_cart.model.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SizeMapper {

    private final ProductMapper productMapper;

    public SizeDTO toSizeDTO(Size size) {
        SizeDTO sizeDTO = new SizeDTO();
        sizeDTO.setId(size.getId());
        sizeDTO.setSizeName(size.getSizeName());
        sizeDTO.setQuantity(size.getQuantity());
        sizeDTO.setProduct(productMapper.toProductDTO(size.getProduct()));
        return sizeDTO;
    }
}
