package com.budget.auth.service;

import com.budget.auth.client.LoginClient;
import com.budget.auth.client.RegisterClient;
import com.budget.auth.client.TxnWarmupClient;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public FeignResponseDTO register (RegisterDto user){
        String hashedPassword = util.stringEncoder(user.getPassword());
        user.setPassword(hashedPassword);
        String header = util.internalSignatureHandler(SERVICE);
        ResponseEntity<FeignResponseDTO> res = registerClient.register("register", header, util.stringify(user));
        return res.getBody();
    }

    public FeignResponseDTO login(LoginDto credentials,
                                  HttpServletRequest request) {
        SecurityLogDto log = new SecurityLogDto(credentials.getUsername(), util.passwordFP(credentials.getPassword()), null);
        request.setAttribute("securityLog", log);
        String header = util.internalSignatureHandler(SERVICE);
        InternalFeignDTO res = loginClient.login(header, credentials.getUsername()).getBody();
        if (res == null)
            throw new RuntimeException("loginClient returned status 2xx but null body");
        if (!util.authHandler(res, credentials))
            throw new CustomAccessDeniedException("Password mismatch");
        record UserDetails (String name, String surname){};
        warmupClient.warmup(header);
        return new FeignResponseDTO(new UserDetails(res.getGivenName(), res.getSurname()),
                "Login successful",
                "Auth",
                res.isAdmin());
    }
}
