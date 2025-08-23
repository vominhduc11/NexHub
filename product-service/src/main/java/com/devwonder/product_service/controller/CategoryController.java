package com.devwonder.product_service.controller;

import com.devwonder.product_service.dto.BaseResponse;
import com.devwonder.product_service.entity.Category;
import com.devwonder.product_service.service.CategoryService;
import com.devwonder.product_service.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/product/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new product category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public ResponseEntity<BaseResponse<Category>> createCategory(@RequestBody Category category, HttpServletRequest request) {
        if (!SecurityUtil.hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Admin access required to create categories", "ACCESS_DENIED"));
        }
        
        try {
            Category savedCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(savedCategory, "Category created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error creating category", "INTERNAL_ERROR"));
        }
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all product categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponse<List<Category>>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(BaseResponse.success(categories, "Categories retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving categories", "INTERNAL_ERROR"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<BaseResponse<Category>> updateCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id, 
            @RequestBody Category category, 
            HttpServletRequest request) {
        if (!SecurityUtil.hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Admin access required to update categories", "ACCESS_DENIED"));
        }
        
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(BaseResponse.success(updatedCategory, "Category updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Category not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error updating category", "INTERNAL_ERROR"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete category", description = "Soft delete a category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category soft deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Category already deleted")
    })
    public ResponseEntity<BaseResponse<Void>> softDeleteCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id, 
            HttpServletRequest request) {
        if (!SecurityUtil.hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Admin access required to delete categories", "ACCESS_DENIED"));
        }
        
        try {
            categoryService.softDeleteCategory(id);
            return ResponseEntity.ok(BaseResponse.success("Category soft deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Category not found", "NOT_FOUND"));
            }
            if (e.getMessage().contains("already deleted")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("Category is already deleted", "ALREADY_DELETED"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error soft deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error soft deleting category", "INTERNAL_ERROR"));
        }
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete category", description = "Permanently delete a category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category hard deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<BaseResponse<Void>> hardDeleteCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id, 
            HttpServletRequest request) {
        if (!SecurityUtil.hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Admin access required to hard delete categories", "ACCESS_DENIED"));
        }
        
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(BaseResponse.success("Category hard deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Category not found", "NOT_FOUND"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error hard deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error hard deleting category", "INTERNAL_ERROR"));
        }
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore category", description = "Restore a soft deleted category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category restored successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Category is not deleted")
    })
    public ResponseEntity<BaseResponse<Void>> restoreCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id, 
            HttpServletRequest request) {
        if (!SecurityUtil.hasAdminRole(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Admin access required to restore categories", "ACCESS_DENIED"));
        }
        
        try {
            categoryService.restoreCategory(id);
            return ResponseEntity.ok(BaseResponse.success("Category restored successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("Category not found", "NOT_FOUND"));
            }
            if (e.getMessage().contains("not deleted")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("Category is not deleted", "NOT_DELETED"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Error restoring category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error restoring category", "INTERNAL_ERROR"));
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active categories", description = "Retrieve paginated list of active (non-deleted) categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active categories retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<BaseResponse<Page<Category>>> getAllActiveCategories(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10") 
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<Category> categories = categoryService.getAllActiveCategories(page, size);
            return ResponseEntity.ok(BaseResponse.success(categories, "Active categories retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving active categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Error retrieving active categories", "INTERNAL_ERROR"));
        }
    }

}