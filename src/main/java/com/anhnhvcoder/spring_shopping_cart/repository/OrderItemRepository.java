package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.OrderItem;
import com.anhnhvcoder.spring_shopping_cart.response.ChartResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT new com.anhnhvcoder.spring_shopping_cart.response.ChartResponse(c.name, COUNT(c.name)) " +
            "FROM OrderItem item " +
            "JOIN item.product p " +
            "JOIN p.category c " +
            "GROUP BY c.name")
    List<ChartResponse> findCategoryCounts();
}
