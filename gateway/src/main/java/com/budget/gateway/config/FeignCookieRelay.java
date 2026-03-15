package com.budget.gateway.config;


import feign.RequestInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignCookieRelay {
    @Bean
    public RequestInterceptor interceptor () {
        return requestTemplate -> {
            ServletRequestAttributes att = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (att != null) {
                HttpServletRequest request = att.getRequest();
                Cookie[] cookies = request.getCookies();
                if (cookies != null)
                    for (Cookie c : cookies) {
                        if ("access".equals(c.getName()))
                            requestTemplate.header("Cookie", "access=" + c.getValue());
                    }
            }
        };
    }

}
