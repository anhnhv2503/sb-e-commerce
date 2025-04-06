package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    Page<Order> findByStatusOrderByOrderDateDesc(Pageable pageable, OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = ?1")
    BigDecimal sumTotalAmountByStatus(OrderStatus status);

    Optional<Order> findByOrderCode(Long orderCode);

    List<Order> findTop3ByOrderByOrderDateDesc();

}
