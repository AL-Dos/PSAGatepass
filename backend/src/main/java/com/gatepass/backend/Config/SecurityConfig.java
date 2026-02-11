package com.gatepass.backend.Config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.gatepass.backend.Security.JwtAuthenticationFilter;

import jakarta.servlet.Filter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    @Value("${cors.allowed-origins:}")
    private String allowedOriginsEnv;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/api/submit", "/api/login", "/api/me",
                            "/api/verify/**", "/api/guard/scan", "/api/guard/login")
                        .permitAll()
                        .requestMatchers("/api/list/**").hasRole("ADMIN")
                        .requestMatchers("/api/logout").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore((Filter) jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow exact origins and also support origin patterns for flexibility
        config.setAllowedOrigins(resolveAllowedOrigins());
        config.setAllowedOriginPatterns(resolveAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(resolveAllowedOrigins());
        config.setAllowedOriginPatterns(resolveAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<SimpleCorsFilter> simpleCorsFilterRegistration() {
        FilterRegistrationBean<SimpleCorsFilter> reg = new FilterRegistrationBean<>(new SimpleCorsFilter());
        reg.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    private List<String> resolveAllowedOrigins() {
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            return Arrays.stream(allowedOriginsEnv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
        }

        return List.of(
                "http://localhost:4200",
                "https://localhost:4200",
                "http://localhost:8084",
                "https://localhost:8084",
                "http://127.0.0.1:4200",
                "https://127.0.0.1:4200",
                "http://127.0.0.1:8084",
                "https://127.0.0.1:8084",
                "http://127.0.0.1",
                "https://127.0.0.1",
                "http://localhost",
                "https://localhost",
                "http://gatepass.local",
                "https://gatepass.local",
                "http://gatepass.local:8084",
                "https://gatepass.local:8084",
                "localhost",
                "gatepass.local");
    }
}
