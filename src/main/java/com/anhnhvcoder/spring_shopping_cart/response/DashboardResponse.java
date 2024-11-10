package com.anhnhvcoder.spring_shopping_cart.response;


import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DashboardResponse {

    private Long totalProducts;
    private Long totalCategories;
    private Long totalOrders;
    private Long totalCustomers;
    private BigDecimal totalRevenue;
}
