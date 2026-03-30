package com.budget.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDetails {
    private String dummyProvider;
    private int dummyNumber;
    private LocalDateTime dummyExpiry;
    private int dummyCvv;
}
