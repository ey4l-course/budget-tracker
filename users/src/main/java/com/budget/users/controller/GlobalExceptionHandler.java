package com.budget.users.controller;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.common.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final LogUtil logger;

    public GlobalExceptionHandler (LogUtil logger){
        this.logger =   logger;
    }
    //Internal use
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<FeignResponseDTO> userTakenHandler (DuplicateKeyException e){
        FeignResponseDTO res = new FeignResponseDTO(409, "User already exists");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> unexpectedErrors (Exception e,
                                                    HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setStatusMessage(e.getMessage());
        contextDTO.setOutcome("[FAILURE]");
        String uuid = logger.logRequest(contextDTO);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-log-ID", uuid)
                .body(e.getMessage());
    }

    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO ctx = (RequestContextDTO) request.getAttribute("context");
        return ctx != null ? ctx
                : new RequestContextDTO(request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"));
    }
}
