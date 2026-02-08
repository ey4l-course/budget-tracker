package com.budget.auth.controller;

import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.SecurityLogDto;
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
        return ResponseEntity.status(409).body(e.contentUTF8());
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public  ResponseEntity<FeignResponseDTO> userNotFoundHandler (FeignException.Unauthorized e,
                                                                  HttpServletRequest request){
        SecurityLogDto dto = (SecurityLogDto) request.getAttribute("securityLog");
        dto.setMessage(e.getMessage());
        String uuid = logger.securityLog(dto);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new FeignResponseDTO(uuid,"Auth"));
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<FeignResponseDTO> passwordMismatchHandler (CustomAccessDeniedException e,
                                                                     HttpServletRequest request){
        SecurityLogDto dto = (SecurityLogDto) request.getAttribute("securityLog");
        dto.setMessage(e.getMessage());
        String uuid = logger.securityLog(dto);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new FeignResponseDTO(uuid,"Auth"));
    }

    //Propagate to GW
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> unexpectedDownstreamException (FeignException e){
        if (e.status() == 0){
            String uuid = logger.internalErrorLog(e);
            return ResponseEntity.status(503).body(new FeignResponseDTO(uuid, "Auth"));
        }
        return ResponseEntity.status(e.status()).body(e.contentUTF8());
    }

    //Unpredicted exception. Logs full trace and sends uuid upstream
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FeignResponseDTO> unexpectedErrors (Exception e){
        String uuid = logger.internalErrorLog(e);
        FeignResponseDTO res = new FeignResponseDTO(uuid, "Auth");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}

