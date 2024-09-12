package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {

    Size findBySizeNameAndProductId(String sizeName, Long productId);

    Size findBySizeName(String sizeName);
}
