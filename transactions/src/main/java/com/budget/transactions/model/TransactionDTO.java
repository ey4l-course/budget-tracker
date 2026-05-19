package com.budget.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    private String category;
    private TxnType txnType;
    private String name;
    private LocalDateTime date;
    private BigDecimal amount;
    private String comment;
    private int flag;
}