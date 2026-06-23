package com.enterprise.user.infrastructure.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MdcFilter.class);
    private static final String USER_ID_KEY = "userId";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof com.enterprise.user.domain.model.User) {
                String userId = ((com.enterprise.user.domain.model.User) auth.getPrincipal()).getId().toString();
                MDC.put(USER_ID_KEY, userId);
                logger.info("MDC inyectado con userId: {}", userId);
            } else {
                MDC.put(USER_ID_KEY, "ANONYMOUS");
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear(); // Muy importante limpiar para no filtrar datos entre hilos
        }
    }
}