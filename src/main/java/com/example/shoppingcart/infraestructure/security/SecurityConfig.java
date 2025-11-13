package com.example.shoppingcart.infraestructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(reg -> reg
                .requestMatchers("/actuator/**","/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()

                // ===== GET =====
                .requestMatchers(HttpMethod.GET, "/api/carts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/carts/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/carts/active/user/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/carts/*/total").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/carts/abandoned").hasRole("ADMIN")

                // ===== POST =====
                .requestMatchers(HttpMethod.POST, "/api/carts/create").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/carts/complete/user/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/carts/*/items/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/carts/*/complete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/carts/abandoned/*/notify").hasRole("ADMIN")

                // ===== PUT =====
                .requestMatchers(HttpMethod.PUT, "/api/carts/*/complete").hasRole("ADMIN")

                // ===== DELETE =====
                .requestMatchers(HttpMethod.DELETE, "/api/carts/*/items/*").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/carts/*/clear").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/carts/*").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
