package com.devwonder.api_gateway.config;

import java.util.*;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri("http://auth-service:8081/auth/.well-known/jwks.json")
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))

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
                                "/auth/v3/api-docs").permitAll()

                        // Auth Service - public access
                        .pathMatchers("/api/auth/**").permitAll()

                        // Product Service - GET public, POST/PUT/DELETE require ADMIN or specific
                        // permissions
                        .pathMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .pathMatchers("/api/product-serials/*/exists").permitAll() // For validation
                        .pathMatchers(HttpMethod.POST, "/api/product/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_PRODUCT_CREATE")
                        .pathMatchers(HttpMethod.PUT, "/api/product/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_PRODUCT_UPDATE")
                        .pathMatchers(HttpMethod.DELETE, "/api/product/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_PRODUCT_DELETE")

                        // Blog Service - GET public, POST/PUT/DELETE require ADMIN or specific
                        // permissions
                        .pathMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/blog/comments/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER", "PERM_COMMENT_CREATE")
                        .pathMatchers(HttpMethod.POST, "/api/blog/**").hasAnyAuthority("ROLE_ADMIN", "PERM_BLOG_CREATE")
                        .pathMatchers(HttpMethod.PUT, "/api/blog/**").hasAnyAuthority("ROLE_ADMIN", "PERM_BLOG_UPDATE")
                        .pathMatchers(HttpMethod.DELETE, "/api/blog/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_BLOG_DELETE")

                        // User Service - ADMIN can manage all, users can access their own data
                        .pathMatchers("/api/user/reseller/*/exists").permitAll() // For validation
                        .pathMatchers("/api/customers/*/exists").permitAll() // For validation
                        .pathMatchers(HttpMethod.POST, "/api/user/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "PERM_USER_CREATE")
                        .pathMatchers(HttpMethod.PUT, "/api/user/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "ROLE_CUSTOMER", "PERM_USER_UPDATE")
                        .pathMatchers(HttpMethod.DELETE, "/api/user/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_USER_DELETE")
                        .pathMatchers(HttpMethod.GET, "/api/user/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "ROLE_CUSTOMER", "PERM_USER_READ")

                        // Warranty Service - ADMIN and DEALER can manage, CUSTOMER can view their own
                        .pathMatchers(HttpMethod.GET, "/api/warranty/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "ROLE_CUSTOMER", "PERM_WARRANTY_READ")
                        .pathMatchers(HttpMethod.POST, "/api/warranty/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "PERM_WARRANTY_CREATE")
                        .pathMatchers(HttpMethod.PUT, "/api/warranty/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEALER", "PERM_WARRANTY_UPDATE")
                        .pathMatchers(HttpMethod.DELETE, "/api/warranty/**")
                        .hasAnyAuthority("ROLE_ADMIN", "PERM_WARRANTY_DELETE")

                        // WebSocket endpoints - require ADMIN role
                        .pathMatchers("/api/notification/ws/**").hasAuthority("ROLE_ADMIN")

                        .anyExchange().denyAll())
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            // Extract roles
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> rolesList = (java.util.List<String>) rolesObj;
                rolesList.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .forEach(authorities::add);
            }

            // Extract permissions
            Object permsObj = jwt.getClaim("permissions");
            if (permsObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> permsList = (java.util.List<String>) permsObj;
                permsList.stream()
                        .map(perm -> new SimpleGrantedAuthority("PERM_" + perm))
                        .forEach(authorities::add);
            }

            return authorities.isEmpty()
                    ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                    : new ArrayList<>(authorities);
        });
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
