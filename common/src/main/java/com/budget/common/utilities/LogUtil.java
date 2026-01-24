package com.budget.common.utilities;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.common.dto.SecurityLogDto;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogUtil {
    private final NoArgGenerator uuidGenerator = Generators.timeBasedEpochGenerator();
    private final ExceptionParserUtil parser;
    private final Logger logger = LoggerFactory.getLogger(LogUtil.class);
    private final Logger fullErrorLogger = LoggerFactory.getLogger("fullErrorLogger");
    private final Logger securityLogger = LoggerFactory.getLogger("SEC");
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneOffset.UTC);

    public LogUtil (ExceptionParserUtil parser){
        this.parser = parser;
    }

    public String logRequest (RequestContextDTO request){
        String uuid = null;
        if (LogCategory.UNEXPECTED_ERROR.equals(request.getCategory())){
            uuid = uuidGenerator.generate().toString();
            fullErrorLogger.error("FULL STACK-TRACE [log ID: {}]", uuid, request.getDebug());
        } else if (LogCategory.USER_ERROR.equals(request.getCategory())) {
            uuid = uuidGenerator.generate().toString();
        }
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
        logPayload.put("statusMessage", request.getMessage());
        logPayload.put("startProcess", ISO_FORMAT.format(request.getStartProcess()));
        if (request.getEndProcess() != null)
            logPayload.put("endProcess", ISO_FORMAT.format(request.getEndProcess()));
        if (request.getDebug() != null)
            logPayload.put("debug",  parser.ExceptionParser(request.getDebug()));
        if (!"GET".equals(request.getMethod()))
            logPayload.put("payload", request.getPayload());
        logger.info("request-log", StructuredArguments.entries(logPayload));
        return uuid;
    }

    public String securityLog(SecurityLogDto sec) throws IllegalAccessException{
        String uuid = uuidGenerator.generate().toString();
        Map<String, Object> logPayload = new HashMap<>();
        logPayload.put("uuid", uuid);
        Field[] fields = sec.getClass().getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            logPayload.put(field.getName(), field.get(sec));
        }
        logger.info("security-log", StructuredArguments.entries(logPayload));
        return uuid;
    }
}
