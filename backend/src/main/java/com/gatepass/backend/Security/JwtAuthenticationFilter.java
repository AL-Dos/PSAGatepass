package com.gatepass.backend.Security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            String name = jwtUtil.getName(token);
            List<String> roles = jwtUtil.getAuthorities(token);
            Collection<? extends GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(new User(name, "", authorities), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Authenticated user: { " + name + " } with role: { " + roles + " }");
        }
        else {
            log.trace("Invalid JWT token: { " + token + " }");
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Auth Header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // Cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getServletPath();

        if (method.equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        // Public endpoints that should not be filtered by the JWT filter
        if (path.equals("/api/login") || path.equals("/api/submit") || path.startsWith("/api/verify")
                || path.equals("/api/guard/login") || path.equals("/api/guard/scan") || path.startsWith("/actuator")) {
            return true;
        }

        return false;
    }
}
