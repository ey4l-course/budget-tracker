package com.budget.auth.service;

import com.budget.auth.client.LoginClient;
import com.budget.auth.client.RegisterClient;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.auth.util.JwtUtil;
import com.budget.common.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class IdentityService {
    private final ObjectMapper mapper;
    private final BCryptPasswordEncoder encoder;
    private final RegisterClient registerClient;
    private final LoginClient loginClient;
    private final JwtUtil jwt;

    public IdentityService (ObjectMapper mapper,
                            BCryptPasswordEncoder encoder,
                            RegisterClient registerClient,
                            LoginClient loginClient,
                            JwtUtil jwt){
        this.mapper = mapper;
        this.encoder = encoder;
        this.registerClient = registerClient;
        this.loginClient = loginClient;
        this.jwt = jwt;
    }

    public FeignResponseDTO register (RegisterDto user){
        String hashedPassword = stringEncoder(user.getPassword());
        user.setPassword(hashedPassword);
            ResponseEntity<FeignResponseDTO> res = registerClient.register("register", stringify(user));
            return res.getBody();
    }

    public FeignResponseDTO login(LoginDto credentials) {
            ResponseEntity<InternalFeignDTO> res = loginClient.login(credentials.getUsername());
            InternalFeignDTO dto = res.getBody();
            if (res.getStatusCode().is4xxClientError())
                throw new CustomAccessDeniedException(new SecurityLogDto(credentials.getUsername(), null, dto.getMsg()));
            if (res.getStatusCode().is2xxSuccessful()) {
                authHandler(dto, credentials);
                return new FeignResponseDTO(200, "Login successful", "Auth", dto.isAdmin());
            }
            return new FeignResponseDTO(500, res.getBody().getMsg(), "Users");
    }

    private String stringEncoder (String raw){
        return encoder.encode(raw);
    }

    private String stringify (Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private void authHandler (InternalFeignDTO dto, LoginDto credentials){
        if (encoder.matches(credentials.getPassword(), dto.getMsg())) {
            credentials.setAccess(jwt.generateAccessToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
            credentials.setRefresh(jwt.generateRefreshToken(credentials.getUsername(), dto.isAdmin() ? "admin" : "user"));
        }else {
            throw new CustomAccessDeniedException(new SecurityLogDto(credentials.getUsername(), credentials.getPassword(), "Wrong password"));
        }
    }
}
