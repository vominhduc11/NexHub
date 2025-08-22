package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.LoginRequest;
import com.devwonder.auth_service.dto.LoginResponse;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.entity.Permission;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        public LoginResponse login(LoginRequest request) {
                log.info("Login attempt for username: {} with userType: {}", request.getUsername(),
                                request.getUserType());

                // Find account by username
                Account account = accountRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

                // Verify password
                if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
                        log.warn("Invalid password for username: {}", request.getUsername());
                        throw new BadCredentialsException("Invalid username or password");
                }

                // Verify user has the requested role type
                boolean hasRequestedRole = account.getRoles().stream()
                                .anyMatch(role -> role.getName().equalsIgnoreCase(request.getUserType()));

                if (!hasRequestedRole) {
                        log.warn("User {} does not have role: {}", request.getUsername(), request.getUserType());
                        throw new BadCredentialsException("User does not have the requested role");
                }

                // Extract roles and permissions
                Set<String> roles = account.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet());

                Set<String> permissions = account.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(Permission::getName)
                                .collect(Collectors.toSet());

                // Generate JWT token
                String token = jwtUtil.generateToken(
                                account.getId(),
                                account.getUsername(),
                                request.getUserType(),
                                roles,
                                permissions);

                // Create user info
                LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                                account.getId(),
                                account.getUsername(),
                                request.getUserType(),
                                roles,
                                permissions);

                log.info("Login successful for username: {} with userType: {}", request.getUsername(),
                                request.getUserType());

                return new LoginResponse(token, userInfo);
        }
}