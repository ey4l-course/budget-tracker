package com.budget.common;

import com.budget.common.dto.AuthenticatedDTO;
import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.SecurityLogDto;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.exceptions.CustomSecurityException;
import com.budget.common.utilities.LogUtil;
import com.budget.common.utilities.SignatureHandlerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final SignatureHandlerUtil sig;
    private final LogUtil logger;
    private final ObjectMapper mapper;

    @Value("${server.name}")
    private String SERVICE;

    public JwtAuthFilter (SignatureHandlerUtil sig,
                          LogUtil logger,
                          ObjectMapper mapper) {
        this.sig = sig;
        this.logger = logger;
        this.mapper = mapper;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
                                    ) throws ServletException, IOException {
        AuthenticatedDTO currentUser = null;
        try {
            String internalSig = request.getHeader("X-internal-Auth");
            if (internalSig != null) {
                currentUser = new AuthenticatedDTO(
                        sig.signatureVerification(internalSig, false),
                        "app"
                );
                setSecurityContext(currentUser);
                filterChain.doFilter(request, response);
                return;
            }

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("access".equals(c.getName()))
                        currentUser = sig.extractDetails(c.getValue());
                }

                if (currentUser != null) {
                    setSecurityContext(currentUser);
                    filterChain.doFilter(request, response);
                }
            }
            String uuid = logger.securityLog(new SecurityLogDto(SERVICE, "", "Missing identifier"));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(mapper.writeValueAsString(new FeignResponseDTO(uuid, SERVICE)));
            return;
        }catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writeValueAsString(new FeignResponseDTO("token expired", SERVICE)));
        }catch (CustomSecurityException e){
            String uuid = logger.securityLog(new SecurityLogDto(SERVICE,
                    e.getFaultyToken(),
                    e.getMessage()
                    ));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writeValueAsString(new FeignResponseDTO(uuid, SERVICE)));
            return;
        }catch (CriticalIncidentException e){
            String uuid = logger.internalErrorLog(e);
            response.setHeader("severity", "fatal");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(mapper.writeValueAsString(new FeignResponseDTO(uuid, SERVICE)));
            return;
        }

        }
        private void setSecurityContext (AuthenticatedDTO currentUser){
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
