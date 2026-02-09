package com.budget.auth.util;

import com.budget.common.dto.SecurityLogDto;
import lombok.Getter;

@Getter
public class CustomAccessDeniedException extends RuntimeException {
    private final Object payload;

    public CustomAccessDeniedException(Object payload){
        this.payload = payload;
    }
}
