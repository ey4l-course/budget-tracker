package com.budget.gateway.controller;

import com.budget.common.dto.*;
import com.budget.gateway.dto.ValidationDTO;
import com.budget.gateway.service.GatewayPublicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class GatewayPublicController {
    private final GatewayPublicService service;

    public GatewayPublicController(GatewayPublicService service){
        this.service = service;
    }
    //Public endpoints

    @GetMapping("/check-username")
    public void isUserNameAvailable (@RequestParam("username") String username,
                                                 HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(username);
        ValidationDTO dto = new ValidationDTO("username", username, null);
        contextDTO.setMessage(dto);
        boolean res = service.checkUsernameAvailability(username);
        dto.setMessage(res ? "Username available" : "Username taken");
        markSuccess(contextDTO, HttpStatus.OK, dto);
    }

    @PostMapping("/register")
    public void register (@RequestBody RegisterDto user,
                                            HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        if (service.register(user).isSameCodeAs(HttpStatus.CREATED))
            markSuccess(contextDTO, HttpStatus.CREATED, "User " + contextDTO.getUserName() + " successfully created");
    }

    @PostMapping("/login")
    public void login (@RequestBody LoginDto login,
                       HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(login.getUsername());
        String res = service.login(login, contextDTO);
        markSuccess(contextDTO, HttpStatus.OK, res);
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
                             Object successMsg){
        contextDTO.setOutcome("[SUCCESS]");
        if (contextDTO.getCategory() == null)
            contextDTO.setCategory(LogCategory.OPERATION);
        contextDTO.setStatusCode(statusCode);
        contextDTO.setMessage(successMsg);
    }
}
