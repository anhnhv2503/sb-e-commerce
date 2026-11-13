package com.anhnhvcoder.ecommerce.service;

import com.anhnhvcoder.ecommerce.dto.LowStockDTO;
import com.anhnhvcoder.ecommerce.dto.MonthlyRevenueDTO;
import com.anhnhvcoder.ecommerce.dto.TopProductDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Object> getDashboardData();

    Map<String, Long> getOrderStatusStatistics();

    List<MonthlyRevenueDTO> getMonthlyRevenue(int year);

    List<TopProductDTO> getTopSellingProducts(int limit);

    List<LowStockDTO> getLowStockProducts(int threshold);
}
