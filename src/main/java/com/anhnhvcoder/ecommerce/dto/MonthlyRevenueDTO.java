package com.anhnhvcoder.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonthlyRevenueDTO {

    private int month;
    private BigDecimal revenue;
    private long orders;
}
