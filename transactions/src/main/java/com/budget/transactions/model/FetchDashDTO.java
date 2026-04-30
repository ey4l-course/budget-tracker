package com.budget.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchDashDTO {
    private String username;
    private LocalDateTime start;
    private LocalDateTime end;
}
