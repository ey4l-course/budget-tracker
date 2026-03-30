package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeignLoginDTO {
    private String msg;
    private boolean admin;
    private boolean isActivated;
    private String givenName;
    private String surname;
}
