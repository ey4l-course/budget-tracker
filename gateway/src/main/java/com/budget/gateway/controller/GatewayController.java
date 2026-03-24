package com.budget.gateway.controller;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.client.TxnClient;
import com.budget.gateway.client.UserClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class GatewayController {
    private final TxnClient txnClient;
    private final UserClient userClient;

    public GatewayController (TxnClient txnClient,
                              UserClient userClient){
        this.txnClient = txnClient;
        this.userClient = userClient;
    }

    @GetMapping ("/login")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public void login (HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        String data = txnClient.getTxn();
        contextDTO.setPayload(data);
        markSuccess(contextDTO, HttpStatus.OK, "user profile successfully loaded");
    }

    @GetMapping ("/session-verification")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public void whoAmI (HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        String name = userClient.whoAmI();
        markSuccess(contextDTO, HttpStatus.OK, name);
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
    }
}
