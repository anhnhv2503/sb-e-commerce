package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteAllByCartId(Long id);
    Optional<CartItem> findBySizeId(Long sizeId);
    List<CartItem> findByCartId(Long cartId);
}
