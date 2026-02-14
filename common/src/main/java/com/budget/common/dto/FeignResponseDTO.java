package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class FeignResponseDTO {
    private Object body;
    private final String msg;
    private final String source;
    boolean flag = false;
}
