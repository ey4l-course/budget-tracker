package com.budget.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBudgetConfigDTO {
    private String username;
    private String categoryName;
    private String categoryType;
    private boolean isManual = true;
    private BigDecimal amount;
}
