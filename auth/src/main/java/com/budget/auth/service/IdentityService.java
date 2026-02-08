package com.budget.auth.service;

import com.budget.auth.client.LoginClient;
import com.budget.auth.client.RegisterClient;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.auth.util.JwtUtil;
import com.budget.common.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class IdentityService {
    private final ObjectMapper mapper;
    private final BCryptPasswordEncoder encoder;
    private final RegisterClient registerClient;
    private final LoginClient loginClient;
    private final JwtUtil jwt;
    private final MessageDigest sha256;
    @Value("${security.fingerprint}")
    private String hashKey;

    public IdentityService (ObjectMapper mapper,
                            BCryptPasswordEncoder encoder,
                            RegisterClient registerClient,
                            LoginClient loginClient,
                            JwtUtil jwt,
                            MessageDigest sha256){
        this.mapper = mapper;
        this.encoder = encoder;
        this.registerClient = registerClient;
        this.loginClient = loginClient;
        this.jwt = jwt;
        this.sha256 = sha256;
    }

    public FeignResponseDTO register (RegisterDto user){
        String hashedPassword = stringEncoder(user.getPassword());
        user.setPassword(hashedPassword);
            ResponseEntity<FeignResponseDTO> res = registerClient.register("register", stringify(user));
            return res.getBody();
    }

    public FeignResponseDTO login(LoginDto credentials,
                                  HttpServletRequest request) {
        SecurityLogDto log = new SecurityLogDto(credentials.getUsername(), passwordFP(credentials.getPassword()), null);
        request.setAttribute("securityLog", log);
        InternalFeignDTO res = loginClient.login(credentials.getUsername()).getBody();
        if (res == null)
            throw new RuntimeException("loginClient returned status 2xx but null body");
        if (!authHandler(res, credentials))
            throw new CustomAccessDeniedException("Password mismatch");
        return new FeignResponseDTO("Login successful", "Auth", res.isAdmin());
    }

    private String stringEncoder (String raw){
        return encoder.encode(raw);
    }

    private String passwordFP (String rawPassword){
        byte[] hash = sha256.digest((rawPassword + hashKey).getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    private String stringify (Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private boolean authHandler (InternalFeignDTO dto, LoginDto credentials){
        if (encoder.matches(credentials.getPassword(), dto.getMsg())) {
            credentials.setAccess(jwt.generateAccessToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
            credentials.setRefresh(jwt.generateRefreshToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
            return true;
        }else {
            return false;
        }
    }
}
