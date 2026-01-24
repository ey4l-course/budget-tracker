package com.budget.auth.util;

import com.budget.common.dto.SecurityLogDto;
import lombok.Getter;

@Getter
public class CustomAccessDeniedException extends RuntimeException {
    private final SecurityLogDto payload;


    public CustomAccessDeniedException(SecurityLogDto payload){
        this.payload = payload;
    }
}
