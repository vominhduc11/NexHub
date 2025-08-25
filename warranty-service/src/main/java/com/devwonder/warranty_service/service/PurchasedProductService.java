package com.devwonder.warranty_service.service;

import com.devwonder.warranty_service.dto.PurchasedProductRequest;
import com.devwonder.warranty_service.dto.PurchasedProductResponse;
import com.devwonder.warranty_service.entity.PurchasedProduct;
import com.devwonder.warranty_service.mapper.WarrantyMapper;
import com.devwonder.warranty_service.repository.PurchasedProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PurchasedProductService {
    
    private final PurchasedProductRepository purchasedProductRepository;
    private final WarrantyMapper warrantyMapper;
    private final ValidationService validationService;
    
    // Get all purchased products
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-products", key = "'page:' + #page + ':size:' + #size")
    public Page<PurchasedProductResponse> getAllPurchasedProducts(int page, int size) {
        log.info("Fetching all purchased products - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findAll(pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get purchased products by customer
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-products-by-customer", key = "'customer:' + #customerId + ':page:' + #page + ':size:' + #size")
    public Page<PurchasedProductResponse> getPurchasedProductsByCustomer(Long customerId, int page, int size) {
        log.info("Fetching purchased products for customer: {} - page: {}, size: {}", customerId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findByCustomerId(customerId, pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get purchased products by reseller
    @Transactional(readOnly = true)
    public Page<PurchasedProductResponse> getPurchasedProductsByReseller(Long resellerId, int page, int size) {
        log.info("Fetching purchased products for reseller: {} - page: {}, size: {}", resellerId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findByResellerId(resellerId, pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get active warranties
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-products-active", key = "'page:' + #page + ':size:' + #size")
    public Page<PurchasedProductResponse> getActiveWarranties(int page, int size) {
        log.info("Fetching active warranties - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findActiveWarranties(pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get expired warranties
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-products-expired", key = "'page:' + #page + ':size:' + #size")
    public Page<PurchasedProductResponse> getExpiredWarranties(int page, int size) {
        log.info("Fetching expired warranties - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findExpiredWarranties(pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get warranties expiring soon
    @Transactional(readOnly = true)
    @Cacheable(value = "warranty-products-expiring", key = "'days:' + #days + ':page:' + #page + ':size:' + #size")
    public Page<PurchasedProductResponse> getWarrantiesExpiringSoon(int days, int page, int size) {
        log.info("Fetching warranties expiring within {} days - page: {}, size: {}", days, page, size);
        
        LocalDate targetDate = LocalDate.now().plusDays(days);
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findWarrantiesExpiringSoon(targetDate, pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get warranties by date range
    @Transactional(readOnly = true)
    public Page<PurchasedProductResponse> getWarrantiesByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("Fetching warranties purchased between {} and {} - page: {}, size: {}", startDate, endDate, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchasedProduct> products = purchasedProductRepository.findByPurchaseDateRange(startDate, endDate, pageable);
        
        return products.map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get customer warranties expiring soon (for notifications)
    @Transactional(readOnly = true)
    public List<PurchasedProductResponse> getCustomerWarrantiesExpiringSoon(Long customerId, int days) {
        log.info("Fetching warranties expiring within {} days for customer: {}", days, customerId);
        
        LocalDate targetDate = LocalDate.now().plusDays(days);
        List<PurchasedProduct> products = purchasedProductRepository.findCustomerWarrantiesExpiringSoon(customerId, targetDate);
        
        return products.stream()
            .map(warrantyMapper::toPurchasedProductResponse)
            .collect(Collectors.toList());
    }
    
    // Get purchased product by ID
    @Transactional(readOnly = true)
    public Optional<PurchasedProductResponse> getPurchasedProductById(Long id) {
        log.info("Fetching purchased product by ID: {}", id);
        
        return purchasedProductRepository.findById(id)
            .map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Register new purchased product
    @CacheEvict(value = {"warranty-products", "warranty-products-by-customer", "warranty-products-active", "warranty-products-expiring"}, allEntries = true)
    public PurchasedProductResponse registerPurchasedProduct(PurchasedProductRequest request) {
        log.info("Registering new purchased product for customer: {}", request.getIdCustomer());
        
        // Validate foreign key references
        validationService.validatePurchasedProductReferences(
            request.getIdProductSerial(), 
            request.getIdReseller(), 
            request.getIdCustomer()
        );
        
        // Check if product serial is already registered
        if (purchasedProductRepository.existsByIdProductSerial(request.getIdProductSerial())) {
            throw new IllegalArgumentException("Product with serial ID " + request.getIdProductSerial() + " is already registered");
        }
        
        // Validate dates
        if (request.getExpirationDate().isBefore(request.getPurchaseDate())) {
            throw new IllegalArgumentException("Expiration date must be after purchase date");
        }
        
        PurchasedProduct purchasedProduct = warrantyMapper.toPurchasedProductEntity(request);
        PurchasedProduct savedProduct = purchasedProductRepository.save(purchasedProduct);
        
        log.info("Purchased product registered successfully with ID: {}", savedProduct.getId());
        return warrantyMapper.toPurchasedProductResponse(savedProduct);
    }
    
    // Update purchased product
    @CacheEvict(value = {"warranty-products", "warranty-products-by-customer", "warranty-products-active", "warranty-products-expired", "warranty-products-expiring"}, allEntries = true)
    public PurchasedProductResponse updatePurchasedProduct(Long id, PurchasedProductRequest request) {
        log.info("Updating purchased product with ID: {}", id);
        
        PurchasedProduct purchasedProduct = purchasedProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Purchased product not found with id: " + id));
        
        // Validate foreign key references
        validationService.validatePurchasedProductReferences(
            request.getIdProductSerial(), 
            request.getIdReseller(), 
            request.getIdCustomer()
        );
        
        // Check if product serial is already registered by another product
        if (!purchasedProduct.getIdProductSerial().equals(request.getIdProductSerial()) &&
            purchasedProductRepository.existsByIdProductSerial(request.getIdProductSerial())) {
            throw new IllegalArgumentException("Product with serial ID " + request.getIdProductSerial() + " is already registered");
        }
        
        // Validate dates
        if (request.getExpirationDate().isBefore(request.getPurchaseDate())) {
            throw new IllegalArgumentException("Expiration date must be after purchase date");
        }
        
        // Update fields
        purchasedProduct.setPurchaseDate(request.getPurchaseDate());
        purchasedProduct.setExpirationDate(request.getExpirationDate());
        purchasedProduct.setWarrantyRemainingDays(warrantyMapper.calculateRemainingDays(request.getExpirationDate()));
        purchasedProduct.setIdProductSerial(request.getIdProductSerial());
        purchasedProduct.setIdReseller(request.getIdReseller());
        purchasedProduct.setIdCustomer(request.getIdCustomer());
        
        PurchasedProduct updatedProduct = purchasedProductRepository.save(purchasedProduct);
        log.info("Purchased product updated successfully: {}", updatedProduct.getId());
        
        return warrantyMapper.toPurchasedProductResponse(updatedProduct);
    }
    
    // Delete purchased product
    @CacheEvict(value = {"warranty-products", "warranty-products-by-customer", "warranty-products-active", "warranty-products-expired", "warranty-products-expiring"}, allEntries = true)
    public void deletePurchasedProduct(Long id) {
        log.info("Deleting purchased product with ID: {}", id);
        
        PurchasedProduct purchasedProduct = purchasedProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Purchased product not found with id: " + id));
        
        purchasedProductRepository.delete(purchasedProduct);
        log.info("Purchased product deleted successfully: {}", id);
    }
    
    // Update warranty remaining days
    public void updateWarrantyRemainingDays(Long id) {
        log.info("Updating warranty remaining days for purchased product: {}", id);
        
        if (!purchasedProductRepository.existsById(id)) {
            throw new RuntimeException("Purchased product not found with id: " + id);
        }
        
        purchasedProductRepository.updateWarrantyRemainingDays(id);
        log.info("Warranty remaining days updated for product: {}", id);
    }
    
    // Update all warranty remaining days (scheduled task)
    public void updateAllWarrantyRemainingDays() {
        log.info("Updating warranty remaining days for all products");
        
        purchasedProductRepository.updateAllWarrantyRemainingDays();
        log.info("All warranty remaining days updated successfully");
    }
    
    // Verify warranty by product serial and customer
    @Transactional(readOnly = true)
    public Optional<PurchasedProductResponse> verifyWarranty(Long productSerialId, Long customerId) {
        log.info("Verifying warranty for product serial: {} and customer: {}", productSerialId, customerId);
        
        return purchasedProductRepository.findByIdProductSerialAndIdCustomer(productSerialId, customerId)
            .map(warrantyMapper::toPurchasedProductResponse);
    }
    
    // Get warranty counts by customer
    @Transactional(readOnly = true)
    public Long getWarrantyCountByCustomer(Long customerId) {
        log.info("Getting warranty count for customer: {}", customerId);
        return purchasedProductRepository.countByCustomerId(customerId);
    }
    
    // Get warranty counts by reseller
    @Transactional(readOnly = true)
    public Long getWarrantyCountByReseller(Long resellerId) {
        log.info("Getting warranty count for reseller: {}", resellerId);
        return purchasedProductRepository.countByResellerId(resellerId);
    }
}