package com.devwonder.product_service.controller;

import com.devwonder.product_service.dto.BaseResponse;
import com.devwonder.product_service.dto.ProductVideoRequest;
import com.devwonder.product_service.dto.ProductVideoResponse;
import com.devwonder.product_service.service.ProductVideoService;
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
@RequestMapping("/product/products/{productId}/videos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Videos", description = "APIs for managing product videos")
public class ProductVideoController {

    private final ProductVideoService productVideoService;

    @GetMapping
    @Operation(summary = "Get product videos", description = "Retrieve all videos for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Videos retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponse<List<ProductVideoResponse>>> getProductVideos(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId) {
        
        log.info("GET /products/{}/videos - Fetching product videos", productId);
        
        try {
            List<ProductVideoResponse> videos = productVideoService.getProductVideos(productId);
            return ResponseEntity.ok(BaseResponse.success(videos, "Product videos retrieved successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Product not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error retrieving product videos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving product videos", "INTERNAL_ERROR"));
        }
    }

    @PostMapping
    @Operation(summary = "Add product video", description = "Add a video to a specific product. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Video added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid video data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<ProductVideoResponse>> addProductVideo(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Valid @RequestBody ProductVideoRequest videoRequest,
            HttpServletRequest request) {
        
        log.info("POST /products/{}/videos - Adding product video", productId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for adding product videos");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            ProductVideoResponse addedVideo = productVideoService.addProductVideo(productId, videoRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(addedVideo, "Product video added successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Product not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error adding product video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error adding product video", "INTERNAL_ERROR"));
        }
    }

    @PutMapping("/{videoId}")
    @Operation(summary = "Update product video", description = "Update a specific product video. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Video updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid video data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product or video not found")
    })
    public ResponseEntity<BaseResponse<ProductVideoResponse>> updateProductVideo(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Video ID", example = "1") @PathVariable Long videoId,
            @Valid @RequestBody ProductVideoRequest videoRequest,
            HttpServletRequest request) {
        
        log.info("PUT /products/{}/videos/{} - Updating product video", productId, videoId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for updating product videos");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            ProductVideoResponse updatedVideo = productVideoService.updateProductVideo(productId, videoId, videoRequest);
            return ResponseEntity.ok(BaseResponse.success(updatedVideo, "Product video updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage(), "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error updating product video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error updating product video", "INTERNAL_ERROR"));
        }
    }

    @DeleteMapping("/{videoId}")
    @Operation(summary = "Delete product video", description = "Delete a specific product video. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Video deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product or video not found")
    })
    public ResponseEntity<BaseResponse<Void>> deleteProductVideo(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Video ID", example = "1") @PathVariable Long videoId,
            HttpServletRequest request) {
        
        log.info("DELETE /products/{}/videos/{} - Deleting product video", productId, videoId);
        
        // Check for ADMIN role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for deleting product videos");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access denied - ADMIN role required", "ACCESS_DENIED"));
        }
        
        try {
            productVideoService.deleteProductVideo(productId, videoId);
            return ResponseEntity.ok(BaseResponse.success("Product video deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage(), "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error deleting product video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error deleting product video", "INTERNAL_ERROR"));
        }
    }
}