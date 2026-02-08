package com.budget.users.controller;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.InternalFeignDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.common.utilities.LogUtil;
import com.budget.users.util.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final LogUtil logger;

    public GlobalExceptionHandler (LogUtil logger){
        this.logger =   logger;
    }
    //User already exists (register)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<FeignResponseDTO> userTakenHandler (DuplicateKeyException e){
        FeignResponseDTO res = new FeignResponseDTO("User already exists", "Users");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    //User not found (login)
    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundHandler (UserNotFoundException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

     //Unpredicted exception. Logs full trace and sends uuid upstream
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FeignResponseDTO> unexpectedErrors (Exception e){
        String uuid = logger.internalErrorLog(e);
        FeignResponseDTO res = new FeignResponseDTO(uuid, "Users");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO ctx = (RequestContextDTO) request.getAttribute("context");
        return ctx != null ? ctx
                : new RequestContextDTO(request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"));
    }

}
