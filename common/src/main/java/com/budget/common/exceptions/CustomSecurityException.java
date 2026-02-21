package com.budget.common.exceptions;

import lombok.Getter;

@Getter
public class CustomSecurityException extends RuntimeException {
    private final String type;
    private final String faultyToken;
    public CustomSecurityException(String message, String type, String faultyToken)
    {
        super(message);
        this.type = type;
        this.faultyToken = faultyToken;
    }
}
