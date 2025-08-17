package com.devwonder.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/auth/**", "/auth/**", "/api/public/**", "/api/auth/.well-known/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/resellers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/user/resellers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/content/blogs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/content/blogs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/content/blogs").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/content/blogs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/content/blogs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/dealer/**").hasRole("DEALER")
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/auth/validate").authenticated()
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwkSetUri("http://auth-service:8081/auth/.well-known/jwks.json")
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> rolesList = (List<String>) rolesObj;
                return rolesList.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(java.util.stream.Collectors.toList());
            }
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        });
        return jwtConverter;
    }
}
