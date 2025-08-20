package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.AccountDto;
import com.devwonder.auth_service.dto.CreateAccountRequest;
import com.devwonder.auth_service.dto.UpdatePasswordRequest;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AccountDto> getAllAccounts() {
        log.debug("Fetching all accounts");
        return accountRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<AccountDto> getAccountById(Long id) {
        log.debug("Fetching account by id: {}", id);
        return accountRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<AccountDto> getAccountByUsername(String username) {
        log.debug("Fetching account by username: {}", username);
        return accountRepository.findByUsername(username)
                .map(this::convertToDto);
    }

    @Transactional
    public AccountDto createAccount(CreateAccountRequest request) {
        log.info("Creating new account: {}", request.getUsername());
        
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Account with username '" + request.getUsername() + "' already exists");
        }

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());
        
        return convertToDto(savedAccount);
    }

    @Transactional
    public void deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account with id " + id + " not found");
        }
        
        accountRepository.deleteById(id);
        log.info("Account deleted successfully");
    }

    @Transactional
    public AccountDto updatePassword(Long id, UpdatePasswordRequest request) {
        log.info("Updating password for account with id: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        Account updatedAccount = accountRepository.save(account);
        
        log.info("Password updated successfully");
        return convertToDto(updatedAccount);
    }

    public boolean validateCredentials(String username, String password) {
        log.debug("Validating credentials for username: {}", username);
        
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            return passwordEncoder.matches(password, account.getPassword());
        }
        
        return false;
    }

    private AccountDto convertToDto(Account account) {
        return new AccountDto(account.getId(), account.getUsername());
    }
}