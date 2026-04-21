package com.budget.auth.service;

import com.budget.auth.client.LoginClient;
import com.budget.auth.client.RegisterClient;
import com.budget.auth.client.TxnWarmupClient;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentityService {
    private final RegisterClient registerClient;
    private final LoginClient loginClient;
    private final TxnWarmupClient warmupClient;
    private final IdentityUtil util;

    @Value("${server.name}")
    private String SERVICE;

    public IdentityService (
                            RegisterClient registerClient,
                            LoginClient loginClient,
                            TxnWarmupClient warmupClient,
                            IdentityUtil util){
        this.registerClient = registerClient;
        this.loginClient = loginClient;
        this.warmupClient = warmupClient;
        this.util = util;
    }

    public FeignRegisterDTO register (RegisterDto user){
        String hashedPassword = util.stringEncoder(user.getPassword());
        user.setPassword(hashedPassword);
        String header = util.internalSignatureHandler(SERVICE);
        ResponseEntity<FeignRegisterDTO> res = registerClient.register(header, user);
        return res.getBody();
    }

    public FeignLoginDTO login(LoginDto credentials,
                                  HttpServletRequest request) {
        SecurityLogDto log = new SecurityLogDto(credentials.getUsername(), util.passwordFP(credentials.getPassword()), null);
        request.setAttribute("securityLog", log);
        String header = util.internalSignatureHandler(SERVICE);
        FeignLoginDTO res = loginClient.login(header, credentials.getUsername()).getBody();
        if (res == null)
            throw new RuntimeException("loginClient returned status 2xx but null body");
        if (!util.authHandler(res, credentials))
            throw new CustomAccessDeniedException("Password mismatch");
        if (res.isActivated())
            try {
                warmupClient.warmup(header, credentials.getUsername());
            }catch (FeignException e){
                System.out.println("Failed to warm-up: " + e.getMessage());
            }
        res.setMsg("Successful login");
        return res;
    }

    public String getNewToken(AuthenticatedDTO user) { return util.generateNewAccessToken(user); }

    public List<String> logout() {
        return util.logout();
    }
}
