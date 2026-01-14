package com.budget.gateway.controller;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.dto.ValidationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//*********************************************************************************
//* Handlers do not write responses, ContextInitUtil owns the HttpServletResponse *
//*********************************************************************************

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler (IllegalArgumentException.class)
    public void illegalArgumentHandler (IllegalArgumentException e,
                                                          HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.USER_ERROR);
        contextDTO.setStatusCode(HttpStatus.BAD_REQUEST);
        contextDTO.setDebug(e);
        ValidationDTO dto = (ValidationDTO) contextDTO.getMessage();
        dto.setMessage(e.getMessage());
        contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(Exception.class)
    public void unexpectedErrors (Exception e,
                                                         HttpServletRequest request) throws JsonProcessingException {
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setMessage("Internal server error");
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
