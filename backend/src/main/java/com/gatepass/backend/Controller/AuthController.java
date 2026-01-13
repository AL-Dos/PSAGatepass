package com.gatepass.backend.Controller;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatepass.backend.Security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response, HttpServletRequest httpRequest) {
        try {
            log.info("Trying login for { " + request.getName() + " }");
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword())
            );

            log.info("Authentication success for { " + request.getName() + " }");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // create cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) 
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtUtil.getExpirationMs()))
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of("authenticated", true));
        } catch (AuthenticationException e) {
            log.warn("Login failed for { " + request.getName() + " }: " + "{ " + e.getMessage() + " }");
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@CookieValue(required = false) String jwt, HttpServletResponse response) {
        ResponseCookie expired = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
        return ResponseEntity.ok(Map.of("message", "Logged Out"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        return ResponseEntity.ok(
            Map.of("authenticated", auth != null && auth.isAuthenticated())
        );
    }

    @Data
    public static class LoginRequest {
        private String name;
        private String password;
    }   
}
