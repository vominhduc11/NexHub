package com.devwonder.common.config;

import com.devwonder.common.constants.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public abstract class BaseSecurityConfig {

    protected abstract void configureService(HttpSecurity http) throws Exception;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/error").permitAll()
            );
            
        configureService(http);
        
        return http.build();
    }
    
    protected void requireGatewayHeader(HttpSecurity http) throws Exception {
        http.addFilterBefore(new GatewayHeaderFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    private static class GatewayHeaderFilter implements jakarta.servlet.Filter {
        @Override
        public void doFilter(jakarta.servlet.ServletRequest request, 
                           jakarta.servlet.ServletResponse response,
                           jakarta.servlet.FilterChain chain) 
                           throws java.io.IOException, jakarta.servlet.ServletException {
            
            jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
            jakarta.servlet.http.HttpServletResponse httpResponse = (jakarta.servlet.http.HttpServletResponse) response;
            
            String path = httpRequest.getRequestURI();
            if (path.startsWith("/actuator/") || path.startsWith("/swagger-ui/") || 
                path.startsWith("/v3/api-docs/") || path.equals("/swagger-ui.html") ||
                path.equals("/error")) {
                chain.doFilter(request, response);
                return;
            }
            
            String gatewayHeader = httpRequest.getHeader(SecurityConstants.GATEWAY_HEADER);
            if (SecurityConstants.GATEWAY_SECRET.equals(gatewayHeader)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("{\"error\":\"Direct access not allowed\"}");
            }
        }
    }
}