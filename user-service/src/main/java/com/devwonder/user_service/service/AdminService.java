package com.devwonder.user_service.service;

import com.devwonder.user_service.entity.Admin;
import com.devwonder.user_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminByAccountId(Long accountId) {
        return adminRepository.findById(accountId);
    }

    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin updateAdmin(Long accountId, Admin adminDetails) {
        Admin admin = adminRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Admin not found with account_id: " + accountId));

        admin.setUsername(adminDetails.getUsername());

        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long accountId) {
        Admin admin = adminRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Admin not found with account_id: " + accountId));
        adminRepository.delete(admin);
    }

    public boolean existsByAccountId(Long accountId) {
        return adminRepository.existsById(accountId);
    }
}