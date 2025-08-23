package com.devwonder.product_service.controller;

import com.devwonder.product_service.dto.BaseResponse;
import com.devwonder.product_service.dto.ProductImageRequest;
import com.devwonder.product_service.dto.ProductImageResponse;
import com.devwonder.product_service.service.ProductImageService;
import com.devwonder.product_service.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
            return ResponseEntity.ok(BaseResponse.success(images, "Product images retrieved successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Product not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error retrieving product images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving product images", "INTERNAL_ERROR"));
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
            @Valid @RequestBody ProductImageRequest imageRequest,
            HttpServletRequest request) {
        
        log.info("POST /products/{}/images - Adding product image", productId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for adding product images");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            ProductImageResponse addedImage = productImageService.addProductImage(productId, imageRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(addedImage, "Product image added successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Product not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error adding product image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error adding product image", "INTERNAL_ERROR"));
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
            @Valid @RequestBody ProductImageRequest imageRequest,
            HttpServletRequest request) {
        
        log.info("PUT /products/{}/images/{} - Updating product image", productId, imageId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for updating product images");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            ProductImageResponse updatedImage = productImageService.updateProductImage(productId, imageId, imageRequest);
            return ResponseEntity.ok(BaseResponse.success(updatedImage, "Product image updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage(), "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error updating product image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error updating product image", "INTERNAL_ERROR"));
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
            @Parameter(description = "Image ID", example = "1") @PathVariable Long imageId,
            HttpServletRequest request) {
        
        log.info("DELETE /products/{}/images/{} - Deleting product image", productId, imageId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for deleting product images");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            productImageService.deleteProductImage(productId, imageId);
            return ResponseEntity.ok(BaseResponse.success("Product image deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage(), "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error deleting product image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error deleting product image", "INTERNAL_ERROR"));
        }
    }
}