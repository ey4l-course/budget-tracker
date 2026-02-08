package com.budget.gateway.controller;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.dto.ValidationDTO;
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
        contextDTO.setDebug(e);
        if (contextDTO.getMessage() != null) {
            ValidationDTO dto = (ValidationDTO) contextDTO.getMessage();
            dto.setMessage(e.getMessage());
        }else {
            contextDTO.setMessage(e.getMessage());
        }
        contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(FeignException.Conflict.class)
    public void usernameTakenHandler (FeignException.Conflict e,
                                      HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        cacheService.confirmRegistration(contextDTO.getUserName());
        throw new IllegalArgumentException(String.format("Username %s already taken", contextDTO.getUserName()));
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public void downStreamSecurityHandler (FeignException.Unauthorized e,
                                           HttpServletRequest request){
         RequestContextDTO contextDTO = contextHandler(request);
         contextDTO.setCategory(LogCategory.SECURITY);
         try {
             FeignResponseDTO res = mapper.readValue(e.contentUTF8(), FeignResponseDTO.class);
             contextDTO.setUuid(res.getMsg());
             contextDTO.setSource(res.getSource());
         }catch (JsonProcessingException parseError){
             contextDTO.setDebug(parseError);
             contextDTO.setUuid("9ece7ebd448810b3ab4b61510ed29378");
         }
         contextDTO.setOutcome("[REJECTED]");
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public void networkErrorHandler (FeignException.ServiceUnavailable e,
                                     HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        String body = e.contentUTF8();
        unexpectedHandler(e, contextDTO, body);
    }

    @ExceptionHandler(FeignException.InternalServerError.class)
    public void unexpectedDownstreamException (FeignException.InternalServerError e,
                                               HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        String body = e.contentUTF8();
        unexpectedHandler(e, contextDTO, body);
    }

    //GW unexpected errors handler
    @ExceptionHandler(Exception.class)
    public void unexpectedErrors (Exception e,
                                  HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setCategory(LogCategory.UNEXPECTED_ERROR);
        contextDTO.setDebug(e);
        contextDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        contextDTO.setMessage("Internal server error");
        contextDTO.setOutcome("[FAILURE]");
    }

    //Downstream 500 or network errors handler
    private void unexpectedHandler (Exception e, RequestContextDTO contextDTO, String body){
        try {
            FeignResponseDTO res = mapper.readValue(body, FeignResponseDTO.class);
            contextDTO.setUuid(res.getMsg());
            contextDTO.setSource(res.getSource());
        }catch (JsonProcessingException parseError){
            contextDTO.setDebug(e);
            contextDTO.setUuid("499c5ed3b3ec57542b67466ba3e44c06");
        }
        contextDTO.setCategory(LogCategory.INTERNAL);
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
