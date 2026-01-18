package com.budget.gateway.service;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.gateway.client.PublicUserClient;
import com.budget.gateway.dto.LoginDto;
import com.budget.gateway.util.ValidatorsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GatewayPublicService {
    private final PublicUserClient publicUserClient;
    private final ObjectMapper mapper;
    private final UsernameCacheService cacheService;
    private final ValidatorsUtil validators;
    private final BCryptPasswordEncoder encoder;
    public GatewayPublicService(PublicUserClient userClient,
                                ObjectMapper mapper,
                                UsernameCacheService cacheService,
                                ValidatorsUtil validators,
                                BCryptPasswordEncoder encoder){
        this.publicUserClient = userClient;
        this.mapper = mapper;
        this.cacheService = cacheService;
        this.validators = validators;
        this.encoder = encoder;
    }

    public boolean checkUsernameAvailability (String userName) {
        if (validators.isUsernameInvalid(userName))
            throw new IllegalArgumentException("User name must contain letters, digits or ._-$^~");
        return cacheService.tryReserve(userName);
    }

    public HttpStatusCode register(RegisterDto user) {
        validators.validateRegistrationData(user);
        encryptPassword(user);
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(user);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
        ResponseEntity<FeignResponseDTO> result = publicUserClient.forward("register", jsonBody);
        if (!result.getStatusCode().is2xxSuccessful() || result.getBody() == null)
            throw new RuntimeException("publicUserClient.forward failed");
        FeignResponseDTO res = result.getBody();
        if (res.getStatus() == 409){
            cacheService.updateCache(user.getUsername());
            throw new IllegalArgumentException(String.format("Username %s already taken", user.getUsername()));
        }
        if (res.getStatus() == 200)
            cacheService.confirmRegistration(user.getUsername());
        return HttpStatus.CREATED;
    }

    public HttpStatusCode login(LoginDto login) {
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(login);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
        ResponseEntity<FeignResponseDTO> result = publicUserClient.forward("login", jsonBody);
        if(!result.getStatusCode().is2xxSuccessful() || result.getBody() == null)
            throw new RuntimeException("publicUserClient.forward failed");
        return result.getStatusCode();
    }

    private void encryptPassword(RegisterDto user) {
        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));
    }
}
