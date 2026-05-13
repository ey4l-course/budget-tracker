package com.budget.gateway.controller;

import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.gateway.client.PublicUserClient;
import com.budget.gateway.client.TxnClient;
import com.budget.gateway.client.UserClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;


@RestController
@RequestMapping("/app")
@PreAuthorize("hasAnyAuthority('user', 'admin')")
public class GatewayController {
    private final TxnClient txnClient;
    private final UserClient userClient;
    private final PublicUserClient authClient;

    public GatewayController (TxnClient txnClient,
                              UserClient userClient,
                              PublicUserClient authClient){
        this.txnClient = txnClient;
        this.userClient = userClient;
        this.authClient = authClient;
    }

    @GetMapping ("/login")
    public void login (@RequestParam ("flag") String flag,
                       @RequestParam (required = false, name = "month") @DateTimeFormat (pattern = "yyyy-MM") YearMonth month,
                       HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        if ("warmup".equals(flag))
            contextDTO.setPayload(txnClient.getTxn());
        if ("fetch-dashboard".equals(flag))
            contextDTO.setPayload(txnClient.getDashboard(month.toString()));
        markSuccess(contextDTO, HttpStatus.OK, "warmup".equals(flag) ? "user profile loaded": "Fetched transactions for " + month);
    }

    @GetMapping ("/fetch-dashboard/{month}")
    public void getDashPerMonth (@PathVariable ("month") String month,
                       HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        String data = txnClient.getDashboard(month);
        contextDTO.setPayload(data);
        markSuccess(contextDTO, HttpStatus.OK, "Fetched transactions for " + month);
    }

    @GetMapping ("/session-verification")
    public void whoAmI (HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        String name = userClient.whoAmI();
        markSuccess(contextDTO, HttpStatus.OK, name);
    }

    @PostMapping ("/activate-account")
    public void ActivateAccount (@RequestBody String initPoll,
                                 HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setPayload(txnClient.ActivateAccount(initPoll));
        markSuccess(contextDTO, HttpStatus.OK, "Account successfully activated (manual)");
    }

    @PostMapping ("/update-budget-config")
    public void updateBudgetConfig (@RequestBody String updatedData,
                                    HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        markSuccess(contextDTO, HttpStatus.OK, txnClient.updateBudgetConfig(updatedData));
    }

    @PostMapping ("/activate-account-auto")
    public void activateAccountAuto (@RequestBody String bankDetails,
                                    HttpServletRequest request) {
        RequestContextDTO contextDTO = contextHandler(request);
        //TODO: Next step
    }

    @PostMapping("/logout")
    public void logout (@AuthenticationPrincipal AuthenticatedDTO user,
                        HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        contextDTO.setUserName(user.getUsername());
        contextDTO.setCookies(authClient.logout(user));
        contextDTO.setCategory("admin".equals(user.getRole()) ? LogCategory.ADMIN : LogCategory.OPERATION);
        markSuccess(contextDTO, HttpStatus.NO_CONTENT, "Successfully logged out");
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
