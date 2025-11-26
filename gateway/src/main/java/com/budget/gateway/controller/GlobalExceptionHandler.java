package com.budget.gateway.controller;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler (IllegalArgumentException.class)
    public void illegalArgumentHandler (IllegalArgumentException e,
                                                          HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.USER_ERROR);
        contextDTO.setStatusCode(HttpStatus.BAD_REQUEST);
        contextDTO.setDebug(e);
        contextDTO.setStatusMessage(e.getMessage());
        contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(Exception.class)
    public void unexpectedErrors (Exception e,
                                                         HttpServletRequest request) throws JsonProcessingException {
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setStatusMessage("Internal server error");
        contextDTO.setOutcome("[FAILURE]");
    }

    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO ctx = (RequestContextDTO) request.getAttribute("context");
        return ctx != null ? ctx
                : new RequestContextDTO(request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"));
    }
}
