package com.budget.users.controller;

import com.budget.common.dto.FeignRegisterDTO;
import com.budget.common.exceptions.MinorRuntimeException;
import com.budget.common.utilities.LogUtil;
import com.budget.users.util.UserNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    private final LogUtil logger;

    public GlobalExceptionHandler (LogUtil logger){
        this.logger =   logger;
    }
    //User already exists (register)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<FeignRegisterDTO> userTakenHandler (DuplicateKeyException e){
        FeignRegisterDTO res = new FeignRegisterDTO(e.getMessage(), "User already exists", "Users");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    //User not found (login)
    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundHandler (UserNotFoundException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

    @ExceptionHandler (MinorRuntimeException.class)
    public ResponseEntity<String> minorRuntimeHandler (MinorRuntimeException e,
                                                       @AuthenticationPrincipal String username) {
        logger.internalErrorLog(e);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

     //Unpredicted exception. Logs full trace and sends uuid upstream
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FeignRegisterDTO> unexpectedErrors (Exception e){
        String uuid = logger.internalErrorLog(e);
        FeignRegisterDTO res = new FeignRegisterDTO(uuid, "Users");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
