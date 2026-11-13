package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.dto.TopProductDTO;
import com.anhnhvcoder.ecommerce.enums.OrderStatus;
import com.anhnhvcoder.ecommerce.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT new com.anhnhvcoder.ecommerce.dto.TopProductDTO(p.id, p.name, SUM(oi.quantity), SUM(oi.price))
            FROM OrderItem oi
            JOIN oi.order o
            JOIN oi.size s
            JOIN s.product p
            WHERE o.status <> :excludedStatus
            GROUP BY p.id, p.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<TopProductDTO> findTopSellingProducts(@Param("excludedStatus") OrderStatus excludedStatus, Pageable pageable);
}
