package com.budget.gateway.security;

import com.budget.common.dto.*;
import com.budget.common.exceptions.CustomSecurityException;
import com.budget.common.utilities.LogUtil;
import com.budget.gateway.util.GwSignatureHandlerUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class GwAuthFilter extends OncePerRequestFilter {
    private final GwSignatureHandlerUtil sig;
    private final LogUtil logger;

    @Value("${server.name}")
    private String SERVICE;

    public GwAuthFilter (GwSignatureHandlerUtil sig,
                         LogUtil logger){
        this.sig = sig;
        this.logger = logger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        if (contextDTO == null)
                contextDTO = new  RequestContextDTO(request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"));
        AuthenticatedDTO currentUser = null;
        try{
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie c : cookies){
                    if ("access".equals(c.getName())){
                        currentUser = sig.extractDetails(c.getValue());
                        contextDTO.setUserName(currentUser.getUsername());
                    }
                }
            }
            if (currentUser != null)
                setSecurityContext(currentUser);
        }catch (ExpiredJwtException e){
            contextDTO.setOutcome("[EXPIRED]");
            contextDTO.setStatusCode(HttpStatus.UNAUTHORIZED);
            contextDTO.setMessage("session expired");
            contextDTO.setCategory(LogCategory.OPERATION);
            return;
        }catch (CustomSecurityException e) {
            String uuid = logger.securityLog(new SecurityLogDto(SERVICE,
                    e.getFaultyToken(),
                    e.getMessage()
            ));
            contextDTO.setCategory(LogCategory.SECURITY);
            contextDTO.setOutcome("[REJECTED]");
            contextDTO.setSource(SERVICE);
            contextDTO.setStatusCode(HttpStatus.UNAUTHORIZED);
            contextDTO.setMessage("Bad credentials");
            contextDTO.setUuid(uuid);
            return;
        }
    filterChain.doFilter(request, response);
    }

    private void setSecurityContext (AuthenticatedDTO currentUser) {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        List.of(new SimpleGrantedAuthority(currentUser.getRole()))
                )
        );
    }
}
