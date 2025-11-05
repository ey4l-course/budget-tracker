package com.budget.common.utilities;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LogUtil {
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public LogUtil (ObjectMapper mapper){
        this.mapper = mapper;
    }

    public String logRequest (RequestContextDTO request){
        String uuid;
        uuid = LogCategory.USER_ERROR.equals(request.getCategory()) || LogCategory.UNEXPECTED_ERROR.equals(request.getCategory())
                ? UUID.randomUUID().toString()
                : null;
        Map<String, Object> logPayload = new HashMap<>();
        if (uuid != null)
            logPayload.put("uuid", uuid);
        logPayload.put("category", request.getCategory());
        logPayload.put("ip", request.getIp());
        logPayload.put("userAgent", request.getUserAgent());
        logPayload.put("userName", request.getUserName());
        logPayload.put("method", request.getMethod());
        logPayload.put("entryRoute", request.getEntryRoute());
        logPayload.put("outcome", request.getOutcome());
        logPayload.put("statusCode", request.getStatusCode());
        logPayload.put("statusMessage", request.getStatusMessage());
        logPayload.put("startProcess", request.getStartProcess());
        logPayload.put("endProcess", request.getEndProcess());
        logPayload.put("debug", request.getDebug());
        if (!"GET".equals(request.getMethod()))
            logPayload.put("payload", request.getPayload());
        logger.info(toJson(logPayload));
        return uuid;
    }

    private String toJson(Map<String, Object> logPayload){
        try {
            return mapper.writeValueAsString(logPayload);
        }catch (JsonProcessingException e){
            return "Unable to serialize log" + e.getMessage();
        }
    }
}
