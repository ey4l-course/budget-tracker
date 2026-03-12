package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalFeignDTO {
    private String msg;
    private boolean admin;
    private String givenName;
    private String surname;
    private int userID;
}
