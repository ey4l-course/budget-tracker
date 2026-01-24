package com.budget.auth.controller;

import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.common.utilities.LogUtil;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final LogUtil logger;

    public GlobalExceptionHandler (LogUtil logger){
        this.logger =   logger;
    }
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<FeignResponseDTO> CustomHandler (CustomAccessDeniedException e) throws IllegalAccessException{
        String uuid = logger.securityLog(e.getPayload());
        return ResponseEntity.badRequest().body(new FeignResponseDTO(401, uuid, "Auth"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<FeignResponseDTO> feignExceptionHandler (FeignException e){
        String url = e.request().url();
        return ResponseEntity.internalServerError().body(new FeignResponseDTO(500, e.getMessage(), URI.create(url).getHost()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FeignResponseDTO> unexpectedErrors (Exception e,
                                                              HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setMessage(e.getMessage());
        contextDTO.setOutcome("[FAILURE]");
        String uuid = logger.logRequest(contextDTO);
        FeignResponseDTO res = new FeignResponseDTO(500, uuid, "Auth");
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

