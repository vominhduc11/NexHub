package com.devwonder.product_service.service;

import com.devwonder.product_service.dto.ProductImageRequest;
import com.devwonder.product_service.dto.ProductImageResponse;
import com.devwonder.product_service.entity.Product;
import com.devwonder.product_service.entity.ProductImage;
import com.devwonder.product_service.mapper.ProductImageMapper;
import com.devwonder.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    public ProductImageResponse addProductImage(Long productId, ProductImageRequest imageRequest) {
        log.info("Adding image to product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Create ProductImage entity using Builder pattern
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .url(imageRequest.getImageUrl())
                .alt(imageRequest.getAltText())
                .orderPosition(imageRequest.getDisplayOrder())
                .build();


        product.getProductImages().add(productImage);
        Product savedProduct = productRepository.save(product);
        
        // Find the saved image to return
        ProductImage savedImage = savedProduct.getProductImages().stream()
            .filter(img -> img.getUrl().equals(imageRequest.getImageUrl()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Failed to save product image"));

        log.info("Product image added successfully with ID: {}", savedImage.getId());
        return productImageMapper.toResponse(savedImage);
    }

    public ProductImageResponse updateProductImage(Long productId, Long imageId, ProductImageRequest imageRequest) {
        log.info("Updating image ID: {} for product ID: {}", imageId, productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductImage productImage = product.getProductImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product image not found with id: " + imageId));

        // Update image fields
        productImage.setUrl(imageRequest.getImageUrl());
        productImage.setAlt(imageRequest.getAltText());
        productImage.setOrderPosition(imageRequest.getDisplayOrder());

        productRepository.save(product);
        log.info("Product image updated successfully: {}", imageId);
        return productImageMapper.toResponse(productImage);
    }

    public void deleteProductImage(Long productId, Long imageId) {
        log.info("Deleting image ID: {} from product ID: {}", imageId, productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductImage productImage = product.getProductImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product image not found with id: " + imageId));

        product.getProductImages().remove(productImage);
        productRepository.save(product);
        log.info("Product image deleted successfully: {}", imageId);
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {
        log.info("Fetching images for product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        return product.getProductImages().stream()
            .map(productImageMapper::toResponse)
            .collect(Collectors.toList());
    }

}