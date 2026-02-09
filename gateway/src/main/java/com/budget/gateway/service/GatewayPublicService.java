package com.budget.gateway.service;

import com.budget.common.dto.*;
import com.budget.gateway.client.PublicUserClient;
import com.budget.gateway.util.ValidatorsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpHeaders;
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
        validators.validateRegistrationData(user);
        ResponseEntity<FeignResponseDTO> result = publicUserClient.forward("register", stringify(user));
        cacheService.confirmRegistration(user.getUsername());
        return result.getStatusCode();
    }

    public void login(LoginDto login,
                                                  RequestContextDTO contextDTO) {
        ResponseEntity<FeignResponseDTO> res = publicUserClient.forward("login", stringify(login));
        FeignResponseDTO authDto = res.getBody();
        if (res.getHeaders().get(HttpHeaders.SET_COOKIE) == null)
            throw new RuntimeException("No cookies found");
        if (authDto.isFlag())
            contextDTO.setCategory(LogCategory.ADMIN);
        contextDTO.setCookies(res.getHeaders().get(HttpHeaders.SET_COOKIE));
    }

    private String stringify(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}