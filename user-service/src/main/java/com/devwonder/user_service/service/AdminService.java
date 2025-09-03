package com.devwonder.user_service.service;

import com.devwonder.user_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final AdminRepository adminRepository;
    
    @Transactional(readOnly = true)
    public boolean existsById(Long accountId) {
        return adminRepository.findByAccountId(accountId) != null;
    }
}