package com.gatepass.back.Security;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                   .subject(userDetails.getUsername())
                   .id(jti)
                   .claim("authorities", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                   .issuedAt(now)
                   .expiration(exp)
                   .signWith(key)
                   .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public String getName(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getJti(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getId();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public List<String> getAuthorities(String token) {
        List<?> rawList = extractAllClaims(token).get("authorities", List.class);
        if (rawList == null) {
            return List.of();
        }

        return rawList.stream().map(Object::toString).toList();
    }
}
