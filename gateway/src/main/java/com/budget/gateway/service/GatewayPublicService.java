package com.budget.gateway.service;

import com.budget.common.dto.*;
import com.budget.gateway.client.PublicUserClient;
import com.budget.gateway.dto.ValidationDTO;
import com.budget.gateway.util.ValidatorsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public String checkUsernameAvailability (String userName) {
        ValidationDTO dto = new ValidationDTO("username", userName, validators.isUsernameInvalid(userName)
                ? "User name must contain letters, digits or ._-$^~"
                : null);
        if (dto.getMessage() != null)
            throw new IllegalArgumentException(stringify(dto));
        dto.setMessage(cacheService.tryReserve(userName) ? "userName available" : "username taken");
        return stringify(dto);
    }

    public String register(RegisterDto user) {
        validators.validateRegistrationData(user);
        ResponseEntity<String> result = publicUserClient.forward("register", stringify(user));
        cacheService.confirmRegistration(user.getUsername());
        return result.getBody();
    }

    public String login(LoginDto login,
                        RequestContextDTO contextDTO) {
        ResponseEntity<String> res = publicUserClient.forward("login", stringify(login));
        if (res.getHeaders().get(HttpHeaders.SET_COOKIE) == null)
            throw new RuntimeException("No cookies found");
        contextDTO.setCookies(res.getHeaders().get(HttpHeaders.SET_COOKIE));
        if (res.getHeaders().containsKey("X-flag"))
            contextDTO.setCategory(LogCategory.ADMIN);
        return res.getBody();
    }

    public List<String> refresh (AuthenticatedDTO user) {
        return publicUserClient.refresh(user);
    }

    private String stringify(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}