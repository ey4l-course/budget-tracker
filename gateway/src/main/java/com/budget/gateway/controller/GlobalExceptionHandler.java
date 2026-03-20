package com.budget.gateway.controller;

import com.budget.common.dto.FeignRegisterDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.service.UsernameCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//*********************************************************************************
//* Handlers do not write responses, ContextInitUtil owns the HttpServletResponse *
//*********************************************************************************

@ControllerAdvice
public class GlobalExceptionHandler {
    private final UsernameCacheService cacheService;
    private final ObjectMapper mapper;

    public GlobalExceptionHandler (UsernameCacheService cacheService,
                                   ObjectMapper mapper){
        this.cacheService = cacheService;
        this.mapper = mapper;
    }
    @ExceptionHandler (IllegalArgumentException.class)
    public void illegalArgumentHandler (IllegalArgumentException e,
                                        HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.USER_ERROR);
        contextDTO.setStatusCode(HttpStatus.BAD_REQUEST);
        contextDTO.setMessage(e.getMessage());
        contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler (FeignException.BadRequest.class)
    public void FeignValidationHandler (FeignException.BadRequest e,
                                        HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.USER_ERROR);
        contextDTO.setStatusCode(HttpStatus.BAD_REQUEST);
        contextDTO.setMessage(e.contentUTF8());
        contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public void downStreamSecurityHandler (FeignException.Unauthorized e,
                                           HttpServletRequest request){
         RequestContextDTO contextDTO = contextHandler(request);
         contextDTO.setMessage(e.getMessage());
         contextDTO.setPayload("{\"error\":\"Bad credentials\"}");
         contextDTO.setSource(e.request().url().split("/")[2]);
         contextDTO.setStatusCode(HttpStatus.valueOf(e.status()));
         contextDTO.setCategory(LogCategory.SECURITY);
         contextDTO.setDebug(e);
         contextDTO.setUuid(e.contentUTF8());
         contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public void networkErrorHandler (FeignException.ServiceUnavailable e,
                                     HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setMessage("Downstream service is down");
        unexpectedHandler(e, contextDTO);
    }

    @ExceptionHandler(FeignException.InternalServerError.class)
    public void unexpectedDownstreamException (FeignException.InternalServerError e,
                                               HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setMessage("Downstream service threw Exception");
        unexpectedHandler(e, contextDTO);
    }

    //GW unexpected errors handler
    @ExceptionHandler(Exception.class)
    public void unexpectedErrors (Exception e,
                                  HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setPayload("{\"error\":\"Internal server error\"}");
        contextDTO.setOutcome("[FAILURE]");
    }

    //Downstream 500 or network errors handler
    private void unexpectedHandler (FeignException e, RequestContextDTO contextDTO){
        contextDTO.setPayload("{\"error\":\"Internal server error\"}");
        contextDTO.setUuid(e.contentUTF8());
        contextDTO.setSource(e.request().url().split("/")[2]);
        contextDTO.setCategory(LogCategory.INTERNAL);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
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
