package com.budget.auth.controller;

import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.FeignRegisterDTO;
import com.budget.common.dto.SecurityLogDto;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.utilities.LogUtil;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final LogUtil logger;

    public GlobalExceptionHandler (LogUtil logger){
        this.logger = logger;
    }
    //Propagate to GW
    @ExceptionHandler(FeignException.Conflict.class)
    public ResponseEntity<String> userExistsHandler (FeignException.Conflict e){
        return ResponseEntity.status(400).body(e.contentUTF8());
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public  ResponseEntity<String> userNotFoundHandler (FeignException.Unauthorized e,
                                                                  HttpServletRequest request){
        SecurityLogDto dto = (SecurityLogDto) request.getAttribute("securityLog");
        dto.setMessage(e.contentUTF8());
        String uuid = logger.securityLog(dto);
        return ResponseEntity.status(401).body(uuid);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> passwordMismatchHandler (CustomAccessDeniedException e,
                                                                     HttpServletRequest request){
        SecurityLogDto dto = (SecurityLogDto) request.getAttribute("securityLog");
        dto.setMessage(e.getPayload().toString());
        String uuid = logger.securityLog(dto);
        return ResponseEntity.status(401).body(uuid);
    }

    //Propagate to GW
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> unexpectedDownstreamException (FeignException e){
        System.out.println(e.status());
        System.out.println(e.toString());
        if (e.status() < 100){
            String uuid = logger.internalErrorLog(e);
            return ResponseEntity.status(503).body(uuid);
        }
        return ResponseEntity.status(e.status()).body(e.contentUTF8());
    }

    @ExceptionHandler(CriticalIncidentException.class)
    public ResponseEntity<String> criticalHandler (CriticalIncidentException e){
        //Trigger SMS/mail broker
        String uuid = logger.internalErrorLog(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uuid);
    }

    //Unpredicted exception. Logs full trace and sends uuid upstream
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> unexpectedErrors (Exception e){
        String uuid = logger.internalErrorLog(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uuid);
    }
}

