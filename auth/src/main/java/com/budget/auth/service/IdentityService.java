package com.budget.auth.service;

import com.budget.auth.client.RegisterClient;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.RegisterDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class IdentityService {
    private final ObjectMapper mapper;
    private final BCryptPasswordEncoder encoder;
    private final RegisterClient registerClient;

    public IdentityService (ObjectMapper mapper,
                            BCryptPasswordEncoder encoder,
                            RegisterClient registerClient) {
        this.mapper = mapper;
        this.encoder = encoder;
        this.registerClient = registerClient;
    }

    public FeignResponseDTO register (RegisterDto user){
        String hashedPassword = stringEncoder(user.getPassword());
        user.setPassword(hashedPassword);
        try {
            ResponseEntity<FeignResponseDTO> res = registerClient.register("register", stringify(user));
            return res.getBody();
        }catch (FeignException e){
            return new FeignResponseDTO(500, e.getMessage(), "Users");
        }
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
}
