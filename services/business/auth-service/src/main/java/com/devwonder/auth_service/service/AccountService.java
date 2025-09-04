package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.CreateAccountRequest;
import com.devwonder.auth_service.dto.CreateAccountResponse;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.enums.AccountType;
import com.devwonder.auth_service.exception.AccountNotFoundException;
import com.devwonder.auth_service.exception.RoleNotFoundException;
import com.devwonder.auth_service.exception.UsernameAlreadyExistsException;
import com.devwonder.auth_service.repository.AccountRepository;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        log.info("=== ENTERING createAccount method for username: {} ===", request.getUsername());
        
        try {
            // Check if username already exists
            log.info("Checking if username '{}' exists in database", request.getUsername());
            
            Optional<Account> existingAccount = accountRepository.findByUsername(request.getUsername());
            if (existingAccount.isPresent()) {
                log.error("Username '{}' already exists in database", request.getUsername());
                throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' already exists");
            }
            
            log.info("Username '{}' is available, proceeding with account creation", request.getUsername());
            
            // Determine account type and role
            AccountType accountType = AccountType.valueOf(request.getAccountType().toUpperCase());
            String roleName = accountType == AccountType.DEALER ? "DEALER" : "CUSTOMER";
            
            // Find role
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new RoleNotFoundException(roleName + " role not found in database");
            }
            
            // Create new account
            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setType(accountType);
            account.setInitialStatus(); // Set PENDING status for DEALER accounts, ACTIVE for CUSTOMER
            
            // Save account first
            Account savedAccount = accountRepository.save(account);
            
            // Assign role
            savedAccount.getRoles().add(role);
            accountRepository.save(savedAccount);
            
            log.info("Successfully created account with ID: {}", savedAccount.getId());
            
            return new CreateAccountResponse(
                savedAccount.getId(),
                savedAccount.getUsername(),
                savedAccount.getStatus().toString(),
                "Account created successfully"
            );
            
        } catch (Exception e) {
            log.error("=== EXCEPTION in createAccount: {} ===", e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional
    public void deleteAccount(Long accountId) {
        log.info("=== ENTERING deleteAccount method for account ID: {} ===", accountId);
        
        try {
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
            
            log.info("Deleting account: {} (username: {})", accountId, account.getUsername());
            
            // Clear roles first to avoid constraint issues
            account.getRoles().clear();
            accountRepository.save(account);
            
            // Delete the account
            accountRepository.delete(account);
            
            log.info("Successfully deleted account with ID: {}", accountId);
            
        } catch (Exception e) {
            log.error("=== EXCEPTION in deleteAccount: {} ===", e.getMessage(), e);
            throw e;
        }
    }
}