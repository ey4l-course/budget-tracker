package com.budget.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetails {
    private String username;
    private int dummyBank;
    private int dummyBranch;
    private int dummyAccount;
}
