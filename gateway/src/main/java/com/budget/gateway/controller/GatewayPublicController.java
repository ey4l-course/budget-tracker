package com.budget.gateway.controller;

import com.budget.common.dto.*;
import com.budget.gateway.service.GatewayPublicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        String res = service.checkUsernameAvailability(username);
        markSuccess(contextDTO, HttpStatus.OK, res);
    }

    @PostMapping("/register")
    public void register (@RequestBody RegisterDto user,
                                            HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        markSuccess(contextDTO, HttpStatus.CREATED, service.register(user));
    }

    @PostMapping("/login")
    public void login (@RequestBody LoginDto login,
                       HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(login.getUsername());
        markSuccess(contextDTO, HttpStatus.OK, service.login(login, contextDTO));
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public void refresh (@AuthenticationPrincipal AuthenticatedDTO user,
                         HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        contextDTO.setCookies(service.refresh(user));
        contextDTO.setCategory("admin".equals(user.getRole()) ? LogCategory.ADMIN : null);
        markSuccess(contextDTO, HttpStatus.NO_CONTENT, "Token successfully refreshed");
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
                             String successMsg){
        contextDTO.setOutcome("[SUCCESS]");
        if (contextDTO.getCategory() == null)
            contextDTO.setCategory(LogCategory.OPERATION);
        contextDTO.setStatusCode(statusCode);
        contextDTO.setMessage(successMsg);
        contextDTO.setPayload(successMsg);
    }
}
