package com.devwonder.product_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.product_service.dto.ProductVideoRequest;
import com.devwonder.product_service.dto.ProductVideoResponse;
import com.devwonder.product_service.service.ProductVideoService;
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
            return ResponseUtil.success("Product videos retrieved successfully", videos);
        } catch (BaseException e) {
            log.error("Error retrieving product videos: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error retrieving product videos: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error retrieving product videos");
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
            @Valid @RequestBody ProductVideoRequest videoRequest) {
        
        log.info("POST /products/{}/videos - Adding product video", productId);
        
        try {
            ProductVideoResponse addedVideo = productVideoService.addProductVideo(productId, videoRequest);
            return ResponseUtil.created("Product video added successfully", addedVideo);
        } catch (BaseException e) {
            log.error("Error retrieving product videos: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error adding product video: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error adding product video");
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
            @Valid @RequestBody ProductVideoRequest videoRequest) {
        
        log.info("PUT /products/{}/videos/{} - Updating product video", productId, videoId);
        
        try {
            ProductVideoResponse updatedVideo = productVideoService.updateProductVideo(productId, videoId, videoRequest);
            return ResponseUtil.success("Product video updated successfully", updatedVideo);
        } catch (BaseException e) {
            log.error("Error updating product video: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error updating product video: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error updating product video");
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
            @Parameter(description = "Video ID", example = "1") @PathVariable Long videoId) {
        
        log.info("DELETE /products/{}/videos/{} - Deleting product video", productId, videoId);
        
        try {
            productVideoService.deleteProductVideo(productId, videoId);
            return ResponseUtil.successVoid("Product video deleted successfully");
        } catch (BaseException e) {
            log.error("Error deleting product video: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error deleting product video: {}", e.getMessage(), e);
            return ResponseUtil.internalError("Error deleting product video");
        }
    }
}