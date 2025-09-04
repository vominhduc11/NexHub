package com.devwonder.product_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.product_service.entity.Category;
import com.devwonder.product_service.service.CategoryService;
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
    public ResponseEntity<BaseResponse<Category>> createCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.createCategory(category);
            return ResponseUtil.created("Category created successfully", savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseUtil.internalError("Error creating category");
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
            return ResponseUtil.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving categories", e);
            return ResponseUtil.internalError("Error retrieving categories");
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
            @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseUtil.success("Category updated successfully", updatedCategory);
        } catch (BaseException e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseUtil.internalError("Error updating category");
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
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        try {
            categoryService.softDeleteCategory(id);
            return ResponseUtil.successVoid("Category soft deleted successfully");
        } catch (BaseException e) {
            log.error("Error soft deleting category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error soft deleting category", e);
            return ResponseUtil.internalError("Error soft deleting category");
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
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseUtil.successVoid("Category hard deleted successfully");
        } catch (BaseException e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error hard deleting category", e);
            return ResponseUtil.internalError("Error hard deleting category");
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
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        try {
            categoryService.restoreCategory(id);
            return ResponseUtil.successVoid("Category restored successfully");
        } catch (BaseException e) {
            log.error("Error restoring category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error restoring category", e);
            return ResponseUtil.internalError("Error restoring category");
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
            return ResponseUtil.success("Active categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving active categories", e);
            return ResponseUtil.internalError("Error retrieving active categories");
        }
    }

}