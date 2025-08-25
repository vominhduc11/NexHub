package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.LoginRequest;
import com.devwonder.auth_service.dto.LoginResponse;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.mapper.AuthMapper;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthMapper authMapper;

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

                // Create user info using mapper
                LoginResponse.UserInfo userInfo = authMapper.toUserInfo(account, request.getUserType());

                // Generate JWT token
                String token = jwtUtil.generateToken(
                                account.getId(),
                                account.getUsername(),
                                request.getUserType(),
                                userInfo.getRoles(),
                                userInfo.getPermissions()
                );

                log.info("Login successful for username: {} with userType: {}", request.getUsername(),
                                request.getUserType());

                return new LoginResponse(token, userInfo);
        }
}