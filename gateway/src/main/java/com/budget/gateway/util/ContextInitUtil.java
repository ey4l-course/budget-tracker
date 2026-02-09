package com.budget.gateway.util;

import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import com.budget.common.utilities.LogUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextInitUtil extends OncePerRequestFilter {
    private final IpUtil ipUtil;
    private final LogUtil logUtil;
    private final ObjectMapper mapper;

    public ContextInitUtil (IpUtil ipUtil,
                            LogUtil logUtil,
                            ObjectMapper mapper){
        this.ipUtil = ipUtil;
        this.logUtil = logUtil;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Initiate DTO and set as request attribute
        RequestContextDTO dto = new RequestContextDTO(request.getRequestURI(), request.getMethod(), request.getHeader("user-agent"));
        request.setAttribute("context", dto);

        //Extract IP address
        try {
            dto.setIp(ipUtil.ExtractIp(request));
        }catch (UnknownHostException e) {
            dto.setIp("[UNRESOLVED] " + e.getMessage());
        }catch (Exception e){
            dto.setDebug(e);
        }
        try {
            filterChain.doFilter(request, response);
        }catch (Exception e){
            dto.setCategory(LogCategory.UNEXPECTED_ERROR);
            dto.setDebug(e);
            dto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            dto.setMessage("Internal server error");
            dto.setOutcome("[REJECTED]");
        } finally {
            //Always perform
            dto.setEndProcess(Instant.now());
            String uuid = logUtil.logRequest(dto);

            if (dto.getStatusCode() == null)
                dto.setStatusCode(HttpStatus.valueOf(response.getStatus()));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            //Add log ID in header if exists
            if (uuid != null)
                response.addHeader("X-log-ID", uuid);

            if (dto.getCookies() != null)
                dto.getCookies().forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));

            //Write body if not already written - Should always be true
            if (!response.isCommitted()){
                try{
                    response.setStatus(dto.getStatusCode().value());
                    if (dto.getMessage() != null) {
                        response.getWriter().write(mapper.writeValueAsString(dto.getMessage()));
                    } else {
                        response.getWriter().write("{}");
                    }
                }catch (JsonMappingException e){
                    response.setStatus(dto.getStatusCode().value());
                    response.getWriter().write("\"Internal Server Error during serialization\"");
                }

            }

        }
    }
}
