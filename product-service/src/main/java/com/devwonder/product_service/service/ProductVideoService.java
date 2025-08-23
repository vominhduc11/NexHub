package com.devwonder.product_service.service;

import com.devwonder.product_service.dto.ProductVideoRequest;
import com.devwonder.product_service.dto.ProductVideoResponse;
import com.devwonder.product_service.entity.Product;
import com.devwonder.product_service.entity.ProductVideo;
import com.devwonder.product_service.mapper.ProductVideoMapper;
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
public class ProductVideoService {

    private final ProductRepository productRepository;
    private final ProductVideoMapper productVideoMapper;

    public ProductVideoResponse addProductVideo(Long productId, ProductVideoRequest videoRequest) {
        log.info("Adding video to product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Create ProductVideo entity using Builder pattern
        ProductVideo productVideo = ProductVideo.builder()
                .product(product)
                .url(videoRequest.getVideoUrl())
                .thumbnail(videoRequest.getThumbnailUrl())
                .title(videoRequest.getTitle())
                .description(videoRequest.getDescription())
                .duration(videoRequest.getDuration())
                .build();

        product.getProductVideos().add(productVideo);
        Product savedProduct = productRepository.save(product);
        
        // Find the saved video to return
        ProductVideo savedVideo = savedProduct.getProductVideos().stream()
            .filter(vid -> vid.getUrl().equals(videoRequest.getVideoUrl()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Failed to save product video"));

        log.info("Product video added successfully with ID: {}", savedVideo.getId());
        return productVideoMapper.toResponse(savedVideo);
    }

    public ProductVideoResponse updateProductVideo(Long productId, Long videoId, ProductVideoRequest videoRequest) {
        log.info("Updating video ID: {} for product ID: {}", videoId, productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductVideo productVideo = product.getProductVideos().stream()
            .filter(vid -> vid.getId().equals(videoId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product video not found with id: " + videoId));

        // Update video fields
        productVideo.setUrl(videoRequest.getVideoUrl());
        productVideo.setThumbnail(videoRequest.getThumbnailUrl());
        productVideo.setTitle(videoRequest.getTitle());
        productVideo.setDescription(videoRequest.getDescription());
        productVideo.setDuration(videoRequest.getDuration());

        productRepository.save(product);
        log.info("Product video updated successfully: {}", videoId);
        return productVideoMapper.toResponse(productVideo);
    }

    public void deleteProductVideo(Long productId, Long videoId) {
        log.info("Deleting video ID: {} from product ID: {}", videoId, productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        ProductVideo productVideo = product.getProductVideos().stream()
            .filter(vid -> vid.getId().equals(videoId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product video not found with id: " + videoId));

        product.getProductVideos().remove(productVideo);
        productRepository.save(product);
        log.info("Product video deleted successfully: {}", videoId);
    }

    @Transactional(readOnly = true)
    public List<ProductVideoResponse> getProductVideos(Long productId) {
        log.info("Fetching videos for product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        return product.getProductVideos().stream()
            .map(productVideoMapper::toResponse)
            .collect(Collectors.toList());
    }

}