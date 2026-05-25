package com.budget.transactions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
private BigDecimal id;
private BigDecimal parentId;
private LocalDateTime timestamp;
private String username;
@JsonProperty("txnName")
private String name;
private BigDecimal amount;
private boolean isSplit;
private String defaultCategory;
@JsonProperty("txnCategory")
private String userDefinedCategory;
private int defaultRegularInterval;
private int userDefinedRegular;
private String comment;
private int systemFlag;
private String categoryType;
@JsonProperty("date")
private LocalDate transactionDate;
}
