package com.devwonder.product_service.service;

import com.devwonder.product_service.dto.ProductRequest;
import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.entity.Category;
import com.devwonder.product_service.entity.Product;
import com.devwonder.product_service.mapper.ProductMapper;
import com.devwonder.product_service.repository.CategoryRepository;
import com.devwonder.product_service.repository.ProductRepository;
import com.devwonder.product_service.exception.ProductNotFoundException;
import com.devwonder.product_service.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size")
    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.info("Fetching products from database - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProducts(pageable);
        
        return products.map(productMapper::toResponse);
    }

    @Cacheable(value = "products-by-category", key = "'cat:' + #categoryId + ':page:' + #page + ':size:' + #size")
    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        log.info("Fetching products by category from database: {} - page: {}, size: {}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProductsByCategory(categoryId, pageable);
        
        return products.map(productMapper::toResponse);
    }

    @Cacheable(value = "products-search", key = "'search:' + #keyword + ':page:' + #page + ':size:' + #size")
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        log.info("Searching products in database with keyword: '{}' - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProductsByKeyword(keyword, pageable);
        
        return products.map(productMapper::toResponse);
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));
        
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.getSku() + "' already exists");
        }
        
        // Create Product entity using Builder pattern
        Product product = Product.builder()
                .name(request.getName())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .category(category)
                .specifications(request.getSpecifications())
                .availabilityStatus(request.getAvailabilityStatus() != null ? request.getAvailabilityStatus() : "AVAILABLE")
                .releaseDate(request.getReleaseDate())
                .estimatedDelivery(request.getEstimatedDelivery())
                .warrantyPeriod(request.getWarrantyPeriod())
                .warrantyCoverage(request.getWarrantyCoverage())
                .warrantyConditions(request.getWarrantyConditions())
                .warrantyExcludes(request.getWarrantyExcludes())
                .warrantyRegistrationRequired(request.getWarrantyRegistrationRequired())
                .highlights(request.getHighlights())
                .targetAudience(request.getTargetAudience())
                .useCases(request.getUseCases())
                .popularity(request.getPopularity() != null ? request.getPopularity() : 0)
                .rating(request.getRating())
                .reviewCount(request.getReviewCount() != null ? request.getReviewCount() : 0)
                .tags(request.getTags())
                .sku(request.getSku())
                .relatedProductIds(request.getRelatedProductIds())
                .accessories(request.getAccessories())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : LocalDateTime.now())
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return productMapper.toResponse(savedProduct);
    }

    @Cacheable(value = "product-detail", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ProductResponse> findById(Long id) {
        log.info("Fetching product detail from database for ID: {}", id);
        return productRepository.findById(id)
            .map(productMapper::toResponse);
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search", "product-detail"}, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        // Validation
        if (productRequest.getName() == null || productRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        // Find category
        Category category = categoryRepository.findById(productRequest.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(productRequest.getCategoryId()));

        // Update all fields
        updateProductFields(product, productRequest, category);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getName());
        return productMapper.toResponse(updatedProduct);
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search", "product-detail"}, allEntries = true)
    public ProductResponse patchProduct(Long id, ProductRequest productRequest) {
        log.info("Partially updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        // Update only non-null fields
        if (productRequest.getName() != null && !productRequest.getName().trim().isEmpty()) {
            product.setName(productRequest.getName());
        }
        if (productRequest.getSubtitle() != null) {
            product.setSubtitle(productRequest.getSubtitle());
        }
        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(productRequest.getCategoryId()));
            product.setCategory(category);
        }
        if (productRequest.getSku() != null) {
            product.setSku(productRequest.getSku());
        }
        if (productRequest.getAvailabilityStatus() != null) {
            product.setAvailabilityStatus(productRequest.getAvailabilityStatus());
        }
        if (productRequest.getRating() != null) {
            product.setRating(productRequest.getRating());
        }
        if (productRequest.getPopularity() != null) {
            product.setPopularity(productRequest.getPopularity());
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product partially updated successfully: {}", updatedProduct.getName());
        return productMapper.toResponse(updatedProduct);
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search", "product-detail"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Hard deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
        log.info("Product hard deleted successfully: {}", product.getName());
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search", "product-detail"}, allEntries = true)
    public void softDeleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getDeletedAt() != null) {
            throw new IllegalStateException("Product is already deleted");
        }

        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
        log.info("Product soft deleted successfully: {}", product.getName());
    }

    @CacheEvict(value = {"products", "products-active", "products-by-category", "products-search", "product-detail"}, allEntries = true)
    public void restoreProduct(Long id) {
        log.info("Restoring product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getDeletedAt() == null) {
            throw new IllegalStateException("Product is not deleted");
        }

        product.setDeletedAt(null);
        productRepository.save(product);
        log.info("Product restored successfully: {}", product.getName());
    }

    @Cacheable(value = "products-active", key = "'active:page:' + #page + ':size:' + #size")
    public Page<ProductResponse> getAllActiveProducts(int page, int size) {
        log.info("Fetching all active products from database - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAllActive(pageable);
        
        return products.map(productMapper::toResponse);
    }

    private void updateProductFields(Product product, ProductRequest request, Category category) {
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setDescription(request.getDescription());
        product.setLongDescription(request.getLongDescription());
        product.setCategory(category);
        product.setSpecifications(request.getSpecifications());
        product.setAvailabilityStatus(request.getAvailabilityStatus());
        product.setReleaseDate(request.getReleaseDate());
        product.setEstimatedDelivery(request.getEstimatedDelivery());
        product.setWarrantyPeriod(request.getWarrantyPeriod());
        product.setWarrantyCoverage(request.getWarrantyCoverage());
        product.setWarrantyConditions(request.getWarrantyConditions());
        product.setWarrantyExcludes(request.getWarrantyExcludes());
        product.setWarrantyRegistrationRequired(request.getWarrantyRegistrationRequired());
        product.setHighlights(request.getHighlights());
        product.setTargetAudience(request.getTargetAudience());
        product.setUseCases(request.getUseCases());
        product.setPopularity(request.getPopularity() != null ? request.getPopularity() : product.getPopularity());
        product.setRating(request.getRating());
        product.setReviewCount(request.getReviewCount() != null ? request.getReviewCount() : product.getReviewCount());
        product.setTags(request.getTags());
        product.setSku(request.getSku());
        product.setRelatedProductIds(request.getRelatedProductIds());
        product.setAccessories(request.getAccessories());
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setUpdatedAt(LocalDateTime.now());
    }

}