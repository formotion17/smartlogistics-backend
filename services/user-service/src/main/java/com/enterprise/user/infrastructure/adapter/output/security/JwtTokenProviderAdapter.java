package com.enterprise.user.infrastructure.adapter.output.security;

import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Adaptador de infraestructura que implementa la generación y validación de JWT.
 * <p>
 * Lee la clave secreta y los tiempos de expiración externalizados en el archivo
 * application.properties para garantizar la persistencia de los tokens entre reinicios.
 * </p>
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final String secretKeyStr;
    private final long expirationTime;

    /**
     * Constructor con inyección de propiedades dinámicas de Spring.
     * * @param secretKeyStr Cadena hexadecimal leída de jwt.secret
     * @param expirationTime Tiempo en milisegundos leído de jwt.expiration
     */
    public JwtTokenProviderAdapter(
            @Value("${jwt.secret}") String secretKeyStr,
            @Value("${jwt.expiration}") long expirationTime) {
        this.secretKeyStr = secretKeyStr;
        this.expirationTime = expirationTime;
    }

    /**
     * Transforma la cadena de texto secreta de los properties en una llave criptográfica
     * utilizable por la librería jjwt.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKeyStr);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}