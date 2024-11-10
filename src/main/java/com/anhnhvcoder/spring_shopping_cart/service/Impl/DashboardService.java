package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.repository.CategoryRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.OrderRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import com.anhnhvcoder.spring_shopping_cart.response.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getDashboardData() {

        Long totalProducts = productRepository.count();
        Long totalCategories = categoryRepository.count();
        Long totalOrders = orderRepository.count();
        Long totalCustomers = userRepository.count();
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.DELIVERED);

       return DashboardResponse.builder()
               .totalCategories(totalCategories)
               .totalCustomers(totalCustomers)
               .totalOrders(totalOrders)
               .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
               .build();
    }
}
