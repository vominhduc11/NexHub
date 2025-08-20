package com.devwonder.user_service.service;

import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.repository.ResellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResellerService {

    private final ResellerRepository resellerRepository;

    public List<Reseller> getAllResellers() {
        return resellerRepository.findAll();
    }

    public Optional<Reseller> getResellerByAccountId(Long accountId) {
        return resellerRepository.findById(accountId);
    }

    public Optional<Reseller> getResellerByEmail(String email) {
        return resellerRepository.findByEmail(email);
    }

    public Optional<Reseller> getResellerByPhone(String phone) {
        return resellerRepository.findByPhone(phone);
    }

    public Reseller createReseller(Reseller reseller) {
        return resellerRepository.save(reseller);
    }

    public Reseller updateReseller(Long accountId, Reseller resellerDetails) {
        Reseller reseller = resellerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Reseller not found with account_id: " + accountId));

        reseller.setName(resellerDetails.getName());
        reseller.setAddress(resellerDetails.getAddress());
        reseller.setPhone(resellerDetails.getPhone());
        reseller.setEmail(resellerDetails.getEmail());
        reseller.setDistrict(resellerDetails.getDistrict());
        reseller.setCity(resellerDetails.getCity());

        return resellerRepository.save(reseller);
    }

    public void deleteReseller(Long accountId) {
        Reseller reseller = resellerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Reseller not found with account_id: " + accountId));
        resellerRepository.delete(reseller);
    }

    public boolean existsByAccountId(Long accountId) {
        return resellerRepository.existsById(accountId);
    }
}