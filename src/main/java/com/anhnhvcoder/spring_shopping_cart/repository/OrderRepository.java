package com.anhnhvcoder.spring_shopping_cart.repository;

import com.anhnhvcoder.spring_shopping_cart.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserId(Long userId);
}
