package com.budget.gateway.service;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.gateway.client.PublicUserClient;
import com.budget.gateway.dto.LoginDto;
import com.budget.gateway.util.ValidatorsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayPublicService {
    private final PublicUserClient publicUserClient;
    private final ObjectMapper mapper;
    private final UsernameCacheService cacheService;
    private final ValidatorsUtil validators;
    public GatewayPublicService(PublicUserClient userClient,
                                ObjectMapper mapper,
                                UsernameCacheService cacheService,
                                ValidatorsUtil validators){
        this.publicUserClient = userClient;
        this.mapper = mapper;
        this.cacheService = cacheService;
        this.validators = validators;
    }

    public boolean checkUsernameAvailability (String userName) {
        if (validators.isUsernameInvalid(userName))
            throw new IllegalArgumentException("User name must contain letters, digits or ._-$^~");
        return cacheService.tryReserve(userName);
    }

    public HttpStatusCode register(RegisterDto user) {
        try {
            validators.validateRegistrationData(user);
            ResponseEntity<FeignResponseDTO> result = publicUserClient.forward("register", stringify(user));
            FeignResponseDTO res = result.getBody();
            if (res.getStatus() == 201){
                cacheService.confirmRegistration(user.getUsername());
                return HttpStatus.CREATED;
            }
            if (res.getStatus() == 409) {
                cacheService.updateCache(user.getUsername());
                throw new IllegalArgumentException(String.format("Username %s already taken", user.getUsername()));
            }else {
                throw new RuntimeException(stringify(res));
            }
        }catch (FeignException e){
            throw new RuntimeException(e);
        }
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

    private String stringify(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}