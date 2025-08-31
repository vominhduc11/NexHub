package com.devwonder.user_service.service;

import com.devwonder.user_service.dto.CreateResellerRequest;
import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.exception.EmailAlreadyExistsException;
import com.devwonder.user_service.exception.PhoneAlreadyExistsException;
import com.devwonder.user_service.mapper.ResellerMapper;
import com.devwonder.user_service.repository.ResellerRepository;
import com.devwonder.user_service.exception.ResellerNotFoundException;
import com.devwonder.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerService {
    
    private final ResellerRepository resellerRepository;
    private final ResellerMapper resellerMapper;
    
    @Transactional
    public ResellerResponse createReseller(CreateResellerRequest request) {
        log.info("Creating reseller for account ID: {}", request.getAccountId());
        
        // Check if reseller already exists
        if (resellerRepository.existsById(request.getAccountId())) {
            throw new ValidationException("Reseller with account ID " + request.getAccountId() + " already exists");
        }
        
        // Check if phone already exists
        if (resellerRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new PhoneAlreadyExistsException("Phone number '" + request.getPhone() + "' already exists");
        }
        
        // Check if email already exists
        if (resellerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email '" + request.getEmail() + "' already exists");
        }
        
        // Create new reseller using Builder pattern
        Reseller reseller = Reseller.builder()
                .accountId(request.getAccountId())
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .district(request.getDistrict())
                .city(request.getCity())
                .build();
        
        Reseller savedReseller = resellerRepository.save(reseller);
        
        log.info("Successfully created reseller for account ID: {}", savedReseller.getAccountId());
        
        return resellerMapper.toResponse(savedReseller);
    }

    @Transactional(readOnly = true)
    public Page<ResellerResponse> getAllActiveResellers(int page, int size) {
        log.info("Fetching all active resellers - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Reseller> resellers = resellerRepository.findAllActive(pageable);
        
        return resellers.map(resellerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<ResellerResponse> findActiveById(Long accountId) {
        return resellerRepository.findActiveById(accountId)
            .map(resellerMapper::toResponse);
    }

    @Transactional
    public void softDeleteReseller(Long accountId) {
        log.info("Soft deleting reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        if (reseller.getDeletedAt() != null) {
            throw new ValidationException("Reseller is already deleted");
        }

        reseller.setDeletedAt(LocalDateTime.now());
        resellerRepository.save(reseller);
        log.info("Reseller soft deleted successfully: {}", reseller.getName());
    }

    @Transactional
    public void restoreReseller(Long accountId) {
        log.info("Restoring reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        if (reseller.getDeletedAt() == null) {
            throw new ValidationException("Reseller is not deleted");
        }

        reseller.setDeletedAt(null);
        resellerRepository.save(reseller);
        log.info("Reseller restored successfully: {}", reseller.getName());
    }

    @Transactional
    public void hardDeleteReseller(Long accountId) {
        log.info("Hard deleting reseller with account ID: {}", accountId);
        
        Reseller reseller = resellerRepository.findById(accountId)
            .orElseThrow(() -> new ResellerNotFoundException(accountId));

        resellerRepository.delete(reseller);
        log.info("Reseller hard deleted successfully: {}", reseller.getName());
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long accountId) {
        return resellerRepository.existsById(accountId);
    }
}