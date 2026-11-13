package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteAllByCartId(Long id);

    /** Scoped to one cart: the same size can be in many users' carts. */
    Optional<CartItem> findByCartIdAndSizeId(Long cartId, Long sizeId);

    /** Checkout turns every item into an order item, which needs its size and product. */
    @EntityGraph(attributePaths = {"size", "size.product"})
    List<CartItem> findByCartId(Long cartId);
}
