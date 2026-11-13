package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);

    /** Cart page: items with their size and product in one query (images are batch-loaded). */
    @EntityGraph(attributePaths = {"cartItems", "cartItems.size", "cartItems.size.product"})
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findWithItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT c.totalItems FROM Cart c WHERE c.user.id = :userId")
    Optional<Integer> findTotalItemsByUserId(@Param("userId") Long userId);
}
