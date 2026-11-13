package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.dto.LowStockDTO;
import com.anhnhvcoder.ecommerce.dto.MonthlyRevenueDTO;
import com.anhnhvcoder.ecommerce.dto.TopProductDTO;
import com.anhnhvcoder.ecommerce.enums.OrderStatus;
import com.anhnhvcoder.ecommerce.repository.*;
import com.anhnhvcoder.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;


    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> response = new HashMap<>();
        BigDecimal revenue = orderRepository.sumTotalAmountByStatus(OrderStatus.DELIVERED);
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        response.put("totalOrders", orderRepository.count());
        response.put("totalProducts", productRepository.count());
        response.put("totalUsers", userRepository.count());
        response.put("totalCategories", categoryRepository.count());
        response.put("totalBrands", brandRepository.count());
        response.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        Map<String, Long> ordersByStatus = getOrderStatusStatistics();
        response.put("pendingOrders", ordersByStatus.get(OrderStatus.PENDING.name()));
        response.put("newUsersThisMonth", userRepository.countByCreatedAtGreaterThanEqual(firstDayOfMonth));
        response.put("ordersByStatus", ordersByStatus);
        return response;
    }

    @Override
    public Map<String, Long> getOrderStatusStatistics() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            result.put(status.name(), 0L);
        }
        for (Object[] row : orderRepository.countOrdersGroupByStatus()) {
            if (row[0] != null) {
                result.put(((OrderStatus) row[0]).name(), ((Number) row[1]).longValue());
            }
        }
        return result;
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        List<MonthlyRevenueDTO> months = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            months.add(new MonthlyRevenueDTO(month, BigDecimal.ZERO, 0));
        }
        for (Object[] row : orderRepository.sumRevenueByMonth(OrderStatus.DELIVERED,
                LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1))) {
            int month = ((Number) row[0]).intValue();
            MonthlyRevenueDTO dto = months.get(month - 1);
            dto.setRevenue(toBigDecimal(row[1]));
            dto.setOrders(((Number) row[2]).longValue());
        }
        return months;
    }

    @Override
    public List<TopProductDTO> getTopSellingProducts(int limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        return orderItemRepository.findTopSellingProducts(OrderStatus.CANCELLED, PageRequest.of(0, size));
    }

    @Override
    public List<LowStockDTO> getLowStockProducts(int threshold) {
        return sizeRepository.findLowStock(Math.max(threshold, 0));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(value.toString());
    }
}
