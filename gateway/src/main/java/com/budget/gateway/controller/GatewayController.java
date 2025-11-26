package com.budget.gateway.controller;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RegisterDto;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.service.GatewayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

@RestController
public class GatewayController {
    private final GatewayService service;

    public GatewayController (GatewayService service){
        this.service = service;
    }
    //Public endpoints

    @GetMapping("/check-username")
    public void isUserNameAvailable (@RequestParam("username") String username,
                                                 HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(username);
        boolean res = service.checkUsernameAvailability(username);
        markSuccess(contextDTO, HttpStatus.OK, String.valueOf(res));
    }

    @PostMapping("/register")
    public void register (@RequestBody RegisterDto user,
                                            HttpServletRequest request) throws Exception {
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        if (service.register(user).isSameCodeAs(HttpStatus.CREATED))
            markSuccess(contextDTO, HttpStatus.CREATED, "User " + contextDTO.getUserName() + " successfully created");
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
                             HttpStatusCode statusCode,
                             String statusMsg){
        contextDTO.setOutcome("[SUCCESS]");
        contextDTO.setCategory(LogCategory.OPERATION);
        contextDTO.setStatusCode(statusCode);
        contextDTO.setStatusMessage(statusMsg);
    }
}
