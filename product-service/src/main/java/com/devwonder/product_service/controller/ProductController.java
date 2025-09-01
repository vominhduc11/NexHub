package com.devwonder.product_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.annotation.RequireAdminRole;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.product_service.dto.ProductRequest;
import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "APIs for product management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve paginated list of available products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products - page: {}, size: {}", page, size);
        
        try {
            Page<ProductResponse> products = productService.getAllProducts(page, size);
            return ResponseUtil.success("Products retrieved successfully", products);
        } catch (Exception e) {
            log.error("Error retrieving products", e);
            return ResponseUtil.error("Error retrieving products", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products filtered by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category ID or pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products/category/{} - page: {}, size: {}", categoryId, page, size);
        
        try {
            Page<ProductResponse> products = productService.getProductsByCategory(categoryId, page, size);
            return ResponseUtil.success("Products by category retrieved successfully", products);
        } catch (Exception e) {
            log.error("Error retrieving products by category", e);
            return ResponseUtil.error("Error retrieving products by category", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by keyword in name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> searchProducts(
            @Parameter(description = "Search keyword", example = "laptop")
            @RequestParam String keyword,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products/search?keyword='{}' - page: {}, size: {}", keyword, page, size);
        
        try {
            Page<ProductResponse> products = productService.searchProducts(keyword, page, size);
            return ResponseUtil.success("Search results retrieved successfully", products);
        } catch (Exception e) {
            log.error("Error searching products", e);
            return ResponseUtil.error("Error searching products", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @RequireAdminRole
    @Operation(summary = "Create new product", description = "Create a new product. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "409", description = "Product with SKU already exists")
    })
    public ResponseEntity<BaseResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        
        log.info("POST /products - Creating product: {}", productRequest.getName());
        
        try {
            ProductResponse createdProduct = productService.createProduct(productRequest);
            return ResponseUtil.created("Product created successfully", createdProduct);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error creating product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @RequireAdminRole
    @Operation(summary = "Update product", description = "Update all fields of an existing product. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        
        log.info("PUT /products/{} - Updating product", id);
        
        try {
            ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
            return ResponseUtil.success("Product updated successfully", updatedProduct);
        } catch (BaseException e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error updating product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @RequireAdminRole
    @Operation(summary = "Partially update product", description = "Update specific fields of an existing product. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product partially updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<ProductResponse>> patchProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        
        log.info("PATCH /products/{} - Partially updating product", id);
        
        try {
            ProductResponse updatedProduct = productService.patchProduct(id, productRequest);
            return ResponseUtil.success("Product partially updated successfully", updatedProduct);
        } catch (BaseException e) {
            log.error("Error partially updating product: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error partially updating product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error partially updating product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @RequireAdminRole
    @Operation(summary = "Soft delete product", description = "Soft delete a product by ID. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product soft deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Product already deleted")
    })
    public ResponseEntity<BaseResponse<Void>> softDeleteProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /products/{} - Soft deleting product", id);
        
        try {
            productService.softDeleteProduct(id);
            return ResponseUtil.successVoid("Product soft deleted successfully");
        } catch (BaseException e) {
            log.error("Error soft deleting product: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error soft deleting product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error soft deleting product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/hard")
    @RequireAdminRole
    @Operation(summary = "Hard delete product", description = "Permanently delete a product by ID. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product hard deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<Void>> hardDeleteProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /products/{}/hard - Hard deleting product", id);
        
        try {
            productService.deleteProduct(id);
            return ResponseUtil.successVoid("Product hard deleted successfully");
        } catch (BaseException e) {
            log.error("Error hard deleting product: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error hard deleting product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error hard deleting product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/restore")
    @RequireAdminRole
    @Operation(summary = "Restore product", description = "Restore a soft deleted product by ID. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product restored successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Product is not deleted")
    })
    public ResponseEntity<BaseResponse<Void>> restoreProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        
        log.info("POST /products/{}/restore - Restoring product", id);
        
        try {
            productService.restoreProduct(id);
            return ResponseUtil.successVoid("Product restored successfully");
        } catch (BaseException e) {
            log.error("Error restoring product: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error restoring product: {}", e.getMessage(), e);
            return ResponseUtil.error("Error restoring product", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active products", description = "Retrieve paginated list of active (non-deleted) products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getAllActiveProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /products/active - page: {}, size: {}", page, size);
        
        try {
            Page<ProductResponse> products = productService.getAllActiveProducts(page, size);
            return ResponseUtil.success("Active products retrieved successfully", products);
        } catch (Exception e) {
            log.error("Error retrieving active products", e);
            return ResponseUtil.error("Error retrieving active products", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}