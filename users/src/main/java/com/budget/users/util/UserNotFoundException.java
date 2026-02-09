package com.budget.users.util;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final Object payload;

    public UserNotFoundException (Object payload) { this.payload = payload; }
}
