package com.budget.gateway.service;

import com.budget.common.dto.RegisterDto;
import com.budget.gateway.client.PublicUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayService {
    private final PublicUserClient userClient;
    private final ObjectMapper mapper;
    private final UsernameCacheService cacheService;
    public GatewayService (PublicUserClient userClient,
                           ObjectMapper mapper,
                           UsernameCacheService cacheService){
        this.userClient = userClient;
        this.mapper = mapper;
        this.cacheService = cacheService;
    }

    public boolean checkUsernameAvailability (String userName) {
        return cacheService.tryReserve(userName);
    }

    public HttpStatusCode register(RegisterDto user) throws JsonProcessingException {
        String jsonBody = mapper.writeValueAsString(user);
        ResponseEntity<Void> res = userClient.forward("register", jsonBody);
        if (res.getStatusCode() == HttpStatus.CONFLICT){
            cacheService.updateCache(user.getUsername());
            throw new IllegalArgumentException(String.format("Username %s already taken", user.getUsername()));
        }
        if (res.getStatusCode() == HttpStatus.CREATED)
            cacheService.confirmRegistration(user.getUsername());
        return res.getStatusCode();
    }
}
