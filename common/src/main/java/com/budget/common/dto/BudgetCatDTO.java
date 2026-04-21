package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCatDTO {
    private String categoryName;
    private String categoryType;
    private BigDecimal amount;
}
