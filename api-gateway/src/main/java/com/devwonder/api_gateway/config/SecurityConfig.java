package com.devwonder.api_gateway.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of(
            "http://localhost:3000",
            "http://localhost:8080", 
            "http://localhost:5173"
        ));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // CORS preflight requests - HIGHEST PRIORITY - Allow ALL OPTIONS
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        
                        // Swagger UI - public access for development
                        .pathMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/webjars/**",
                            "/webjars/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/api/*/v3/api-docs/**",
                            "/api/*/swagger-ui/**",
                            "/api/*/webjars/**",
                            "/api/user/v3/api-docs/**",
                            "/api/user/swagger-ui/**", 
                            "/api/user/webjars/**",
                            "/auth/swagger-ui.html",
                            "/auth/swagger-ui/**",
                            "/auth/webjars/**",
                            "/auth/v3/api-docs"
                        ).permitAll()
                        
                        // Auth Service - public access
                        .pathMatchers("/api/auth/**").permitAll()
                        
                        // Product Service - GET public, POST/PUT/DELETE require ADMIN  
                        .pathMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .pathMatchers("/api/product-serials/*/exists").permitAll() // For validation
                        .pathMatchers(HttpMethod.POST, "/api/product/**").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/product/**").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/product/**").hasAnyRole("ADMIN")
                        
                        // Blog Service - GET public, POST/PUT/DELETE require ADMIN or CUSTOMER
                        .pathMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/blog/comments/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/blog/**").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/blog/**").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/blog/**").hasAnyRole("ADMIN")
                        
                        // User Service - ADMIN can manage all, users can access their own data
                        .pathMatchers("/api/user/reseller/*/exists").permitAll() // For validation
                        .pathMatchers("/api/customers/*/exists").permitAll() // For validation
                        .pathMatchers(HttpMethod.POST, "/api/user/**").hasAnyRole("ADMIN", "RESELLER")
                        .pathMatchers(HttpMethod.PUT, "/api/user/**").hasAnyRole("ADMIN", "RESELLER", "CUSTOMER")
                        .pathMatchers(HttpMethod.DELETE, "/api/user/**").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/user/**").hasAnyRole("ADMIN", "RESELLER", "CUSTOMER")
                        
                        // Warranty Service - ADMIN and RESELLER can manage, CUSTOMER can view their own
                        .pathMatchers(HttpMethod.GET, "/api/warranty/**").hasAnyRole("ADMIN", "RESELLER", "CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/warranty/**").hasAnyRole("ADMIN", "RESELLER")
                        .pathMatchers(HttpMethod.PUT, "/api/warranty/**").hasAnyRole("ADMIN", "RESELLER")
                        .pathMatchers(HttpMethod.DELETE, "/api/warranty/**").hasAnyRole("ADMIN")
                        
                        // Notification Service - Internal use, authenticated users only
                        .pathMatchers("/api/notification/**").hasAnyRole("ADMIN", "RESELLER", "CUSTOMER")
                        
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri("http://auth-service:8081/auth/.well-known/jwks.json")
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> rolesList = (java.util.List<String>) rolesObj;
                return rolesList.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(java.util.stream.Collectors.toList());
            }
            // Default role if no roles found
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        });
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
