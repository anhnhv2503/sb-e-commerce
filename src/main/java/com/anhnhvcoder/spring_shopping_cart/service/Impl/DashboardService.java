package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.enums.OrderStatus;
import com.anhnhvcoder.spring_shopping_cart.repository.*;
import com.anhnhvcoder.spring_shopping_cart.response.ChartResponse;
import com.anhnhvcoder.spring_shopping_cart.response.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

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

    public List<ChartResponse> getChart() {
        return orderItemRepository.findCategoryCounts();
    }
}
