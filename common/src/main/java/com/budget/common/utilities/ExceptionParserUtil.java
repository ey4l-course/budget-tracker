package com.budget.common.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionParserUtil {
    private final ObjectMapper mapper;

    public  ExceptionParserUtil (ObjectMapper mapper){ this.mapper = mapper; }

    public Map<String, Object> ExceptionParser (Exception e) {
        Map<String, Object> origin = new HashMap<>();
        origin.put("type", e.getClass().getSimpleName());
        origin.put("class", e.getStackTrace()[0].getClassName());
        origin.put("method", e.getStackTrace()[0].getMethodName());
        origin.put("line-number", e.getStackTrace()[0].getLineNumber());
        return origin;
    }
}
