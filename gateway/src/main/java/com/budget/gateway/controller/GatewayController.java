package com.budget.gateway.controller;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RegisterDto;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {
    private final GatewayService service;

    public GatewayController (GatewayService service){
        this.service = service;
    }
    //Public endpoints
    @PostMapping("/register")
    public ResponseEntity<String> register (RegisterDto user,
                                            HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        service.register(user);
        markSuccess(contextDTO, 201, "User " + contextDTO.getUserName() + " successfully created");
        return ResponseEntity.status(HttpStatus.CREATED).body(contextDTO.getStatusMessage());
    }


    //Helper
    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO ctx = (RequestContextDTO) request.getAttribute("context");
        return ctx != null ? ctx
                : new RequestContextDTO(request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"));
    }

    private void markSuccess(RequestContextDTO contextDTO,
                             int statusCode,
                             String statusMsg){
        contextDTO.setOutcome("[SUCCESS]");
        contextDTO.setCategory(LogCategory.OPERATION);
        contextDTO.setStatusCode(statusCode);
        contextDTO.setStatusMessage(statusMsg);
    }
}
