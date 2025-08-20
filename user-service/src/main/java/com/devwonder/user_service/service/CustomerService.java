package com.devwonder.user_service.service;

import com.devwonder.user_service.entity.Customer;
import com.devwonder.user_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerByAccountId(Long accountId) {
        return customerRepository.findById(accountId);
    }

    public Optional<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long accountId, Customer customerDetails) {
        Customer customer = customerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Customer not found with account_id: " + accountId));

        customer.setName(customerDetails.getName());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long accountId) {
        Customer customer = customerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Customer not found with account_id: " + accountId));
        customerRepository.delete(customer);
    }

    public boolean existsByAccountId(Long accountId) {
        return customerRepository.existsById(accountId);
    }
}