package com.enterprise.user.infrastructure.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de seguridad que limita la frecuencia de peticiones por IP.
 * Protege contra ataques de fuerza bruta y saturación del sistema.
 */
@Component
public class RateLimitFilter implements Filter {

    // Usamos ConcurrentHashMap para guardar un "cubo" de tokens por IP.
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = httpRequest.getRemoteAddr();

        // Obtenemos o creamos un bucket para esta IP
        Bucket bucket = cache.computeIfAbsent(ip, this::createNewBucket);

        // Si tenemos tokens, dejamos pasar. Si no, bloqueamos con 429.
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Has superado el limite de peticiones.");
        }
    }

    /**
     * Define la política: 10 peticiones por minuto.
     */
    private Bucket createNewBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}