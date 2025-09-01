package com.devwonder.product_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.product_service.dto.ProductImageRequest;
import com.devwonder.product_service.dto.ProductImageResponse;
import com.devwonder.product_service.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Images", description = "APIs for managing product images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping
    @Operation(summary = "Get product images", description = "Retrieve all images for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponse<List<ProductImageResponse>>> getProductImages(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId) {
        
        log.info("GET /products/{}/images - Fetching product images", productId);
        
        try {
            List<ProductImageResponse> images = productImageService.getProductImages(productId);
            return ResponseUtil.success("Product images retrieved successfully", images);
        } catch (BaseException e) {
            log.error("Error with product image operation: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error retrieving product images: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error retrieving product images");
        }
    }

    @PostMapping
    @Operation(summary = "Add product image", description = "Add an image to a specific product. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid image data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<ProductImageResponse>> addProductImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest imageRequest) {
        
        log.info("POST /products/{}/images - Adding product image", productId);
        
        try {
            ProductImageResponse addedImage = productImageService.addProductImage(productId, imageRequest);
            return ResponseUtil.created("Product image added successfully", addedImage);
        } catch (BaseException e) {
            log.error("Error with product image operation: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error adding product image: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error adding product image");
        }
    }

    @PutMapping("/{imageId}")
    @Operation(summary = "Update product image", description = "Update a specific product image. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid image data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<BaseResponse<ProductImageResponse>> updateProductImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Image ID", example = "1") @PathVariable Long imageId,
            @Valid @RequestBody ProductImageRequest imageRequest) {
        
        log.info("PUT /products/{}/images/{} - Updating product image", productId, imageId);
        
        try {
            ProductImageResponse updatedImage = productImageService.updateProductImage(productId, imageId, imageRequest);
            return ResponseUtil.success("Product image updated successfully", updatedImage);
        } catch (BaseException e) {
            log.error("Error updating product image: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error updating product image: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error updating product image");
        }
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete product image", description = "Delete a specific product image. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<BaseResponse<Void>> deleteProductImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Image ID", example = "1") @PathVariable Long imageId) {
        
        log.info("DELETE /products/{}/images/{} - Deleting product image", productId, imageId);
        
        try {
            productImageService.deleteProductImage(productId, imageId);
            return ResponseUtil.successVoid("Product image deleted successfully");
        } catch (BaseException e) {
            log.error("Error deleting product image: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error deleting product image: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error deleting product image");
        }
    }
}