package com.devwonder.user_service.config;

import com.devwonder.user_service.entity.Admin;
import com.devwonder.user_service.entity.Customer;
import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.repository.AdminRepository;
import com.devwonder.user_service.repository.CustomerRepository;
import com.devwonder.user_service.repository.ResellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ResellerRepository resellerRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting User Service data initialization...");

        createTestUsersIfNotExists();

        log.info("User Service data initialization completed.");
    }

    private void createTestUsersIfNotExists() {
        // Create test admin if not exists
        createTestAdminIfNotExists(1L, "admin");

        // Create test reseller/dealer if not exists
        createTestResellerIfNotExists(2L, "Dealer Company", "dealer@nexhub.com", "0123456789",
                "123 Business Street", "District 1", "Ho Chi Minh City");

        // Create test customer if not exists
        createTestCustomerIfNotExists(3L, "Customer User");
    }

    private void createTestAdminIfNotExists(Long accountId, String username) {
        if (!adminRepository.existsById(accountId)) {
            Admin admin = Admin.builder()
                    .accountId(accountId)
                    .username(username)
                    .build();

            adminRepository.save(admin);
            log.info("Created test admin with accountId: {}, username: {}", accountId, username);
        } else {
            log.info("Admin with accountId {} already exists, skipping creation", accountId);
        }
    }

    private void createTestResellerIfNotExists(Long accountId, String name, String email, String phone,
            String address, String district, String city) {
        if (!resellerRepository.existsById(accountId)) {
            Reseller reseller = Reseller.builder()
                    .accountId(accountId)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .district(district)
                    .city(city)
                    .build();

            resellerRepository.save(reseller);
            log.info("Created test reseller with accountId: {}, name: {}", accountId, name);
        } else {
            log.info("Reseller with accountId {} already exists, skipping creation", accountId);
        }
    }

    private void createTestCustomerIfNotExists(Long accountId, String name) {
        if (!customerRepository.existsById(accountId)) {
            Customer customer = Customer.builder()
                    .accountId(accountId)
                    .name(name)
                    .build();

            customerRepository.save(customer);
            log.info("Created test customer with accountId: {}, name: {}", accountId, name);
        } else {
            log.info("Customer with accountId {} already exists, skipping creation", accountId);
        }
    }
}