package com.devwonder.user_service.service;

import com.devwonder.user_service.entity.Customer;
import com.devwonder.user_service.repository.CustomerRepository;
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
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Transactional
    public Customer createCustomer(Customer customer) {
        log.info("Creating customer for account ID: {}", customer.getAccountId());
        
        // Check if customer already exists
        if (customerRepository.existsById(customer.getAccountId())) {
            throw new RuntimeException("Customer with account ID " + customer.getAccountId() + " already exists");
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Successfully created customer for account ID: {}", savedCustomer.getAccountId());
        
        return savedCustomer;
    }

    @Transactional(readOnly = true)
    public Page<Customer> getAllActiveCustomers(int page, int size) {
        log.info("Fetching all active customers - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findActiveById(Long accountId) {
        return customerRepository.findActiveById(accountId);
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchActiveCustomers(String name, int page, int size) {
        log.info("Searching active customers with name containing: '{}' - page: {}, size: {}", name, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findActiveByNameContaining(name, pageable);
    }

    @Transactional
    public Customer updateCustomer(Long accountId, Customer customerDetails) {
        log.info("Updating customer with account ID: {}", accountId);
        
        Customer customer = customerRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Customer not found with account ID: " + accountId));

        // Validation
        if (customerDetails.getName() == null || customerDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        customer.setName(customerDetails.getName());
        
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", updatedCustomer.getName());
        return updatedCustomer;
    }

    @Transactional
    public void softDeleteCustomer(Long accountId) {
        log.info("Soft deleting customer with account ID: {}", accountId);
        
        Customer customer = customerRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Customer not found with account ID: " + accountId));

        if (customer.getDeletedAt() != null) {
            throw new IllegalStateException("Customer is already deleted");
        }

        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("Customer soft deleted successfully: {}", customer.getName());
    }

    @Transactional
    public void restoreCustomer(Long accountId) {
        log.info("Restoring customer with account ID: {}", accountId);
        
        Customer customer = customerRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Customer not found with account ID: " + accountId));

        if (customer.getDeletedAt() == null) {
            throw new IllegalStateException("Customer is not deleted");
        }

        customer.setDeletedAt(null);
        customerRepository.save(customer);
        log.info("Customer restored successfully: {}", customer.getName());
    }

    @Transactional
    public void hardDeleteCustomer(Long accountId) {
        log.info("Hard deleting customer with account ID: {}", accountId);
        
        Customer customer = customerRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Customer not found with account ID: " + accountId));

        customerRepository.delete(customer);
        log.info("Customer hard deleted successfully: {}", customer.getName());
    }
}