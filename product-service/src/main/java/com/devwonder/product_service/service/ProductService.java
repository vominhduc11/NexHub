package com.devwonder.product_service.service;

import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.entity.Product;
import com.devwonder.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.info("Fetching products - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProducts(pageable);
        
        return products.map(this::convertToResponse);
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        log.info("Fetching products by category: {} - page: {}, size: {}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProductsByCategory(categoryId, pageable);
        
        return products.map(this::convertToResponse);
    }

    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        log.info("Searching products with keyword: '{}' - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProductsByKeyword(keyword, pageable);
        
        return products.map(this::convertToResponse);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSubtitle(product.getSubtitle());
        response.setDescription(product.getDescription());
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        response.setAvailabilityStatus(product.getAvailabilityStatus());
        response.setEstimatedDelivery(product.getEstimatedDelivery());
        response.setWarrantyPeriod(product.getWarrantyPeriod());
        response.setHighlights(product.getHighlights());
        response.setTargetAudience(product.getTargetAudience());
        response.setPopularity(product.getPopularity());
        response.setRating(product.getRating());
        response.setReviewCount(product.getReviewCount());
        response.setSku(product.getSku());
        response.setPublishedAt(product.getPublishedAt());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}