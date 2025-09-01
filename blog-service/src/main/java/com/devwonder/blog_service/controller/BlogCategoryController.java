package com.devwonder.blog_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.blog_service.dto.BlogCategoryRequest;
import com.devwonder.blog_service.dto.BlogCategoryResponse;
import com.devwonder.blog_service.service.BlogCategoryService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/blog/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog Categories", description = "APIs for blog category management")
public class BlogCategoryController {
    
    private final BlogCategoryService categoryService;
    
    @GetMapping
    @Operation(summary = "Get all visible categories", description = "Retrieve all visible blog categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<BaseResponse<List<BlogCategoryResponse>>> getAllVisibleCategories() {
        log.info("GET /blog/categories - Fetching visible categories");
        
        try {
            List<BlogCategoryResponse> categories = categoryService.getAllVisibleCategories();
            return ResponseUtil.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving categories", e);
            return ResponseUtil.internalError("Error retrieving categories");
        }
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get visible categories with pagination", description = "Retrieve paginated list of visible categories")
    public ResponseEntity<BaseResponse<Page<BlogCategoryResponse>>> getVisibleCategories(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/categories/paginated - page: {}, size: {}", page, size);
        
        try {
            Page<BlogCategoryResponse> categories = categoryService.getVisibleCategories(page, size);
            return ResponseUtil.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving categories", e);
            return ResponseUtil.internalError("Error retrieving categories");
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all categories", description = "Retrieve all categories including invisible ones. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Page<BlogCategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /blog/categories/all - page: {}, size: {}", page, size);
        
        try {
            Page<BlogCategoryResponse> categories = categoryService.getAllCategories(page, size);
            return ResponseUtil.success("All categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving all categories", e);
            return ResponseUtil.internalError("Error retrieving categories");
        }
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieve a category by its slug")
    public ResponseEntity<BaseResponse<BlogCategoryResponse>> getCategoryBySlug(
            @Parameter(description = "Category slug", example = "technology") @PathVariable String slug) {
        
        log.info("GET /blog/categories/slug/{}", slug);
        
        try {
            Optional<BlogCategoryResponse> category = categoryService.getCategoryBySlug(slug);
            if (category.isPresent()) {
                return ResponseUtil.success("Category retrieved successfully", category.get());
            } else {
                return ResponseUtil.notFound("Category not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving category by slug", e);
            return ResponseUtil.internalError("Error retrieving category");
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a category by its ID")
    public ResponseEntity<BaseResponse<BlogCategoryResponse>> getCategoryById(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        
        log.info("GET /blog/categories/{}", id);
        
        try {
            Optional<BlogCategoryResponse> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                return ResponseUtil.success("Category retrieved successfully", category.get());
            } else {
                return ResponseUtil.notFound("Category not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving category by ID", e);
            return ResponseUtil.internalError("Error retrieving category");
        }
    }
    
    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new blog category. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogCategoryResponse>> createCategory(
            @Valid @RequestBody BlogCategoryRequest request) {
        
        log.info("POST /blog/categories - Creating category: {}", request.getName());
        
        try {
            BlogCategoryResponse response = categoryService.createCategory(request);
            return ResponseUtil.created("Category created successfully", response);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseUtil.internalError("Error creating category");
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing blog category. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogCategoryResponse>> updateCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BlogCategoryRequest request) {
        
        log.info("PUT /blog/categories/{} - Updating category", id);
        
        try {
            BlogCategoryResponse response = categoryService.updateCategory(id, request);
            return ResponseUtil.success("Category updated successfully", response);
        } catch (BaseException e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseUtil.internalError("Error updating category");
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a blog category. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        
        log.info("DELETE /blog/categories/{}", id);
        
        try {
            categoryService.deleteCategory(id);
            return ResponseUtil.successVoid("Category deleted successfully");
        } catch (BaseException e) {
            log.error("Error deleting category: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ResponseUtil.internalError("Error deleting category");
        }
    }
    
    @PatchMapping("/{id}/toggle-visibility")
    @Operation(summary = "Toggle category visibility", description = "Toggle the visibility of a category. Requires ADMIN role.")
    public ResponseEntity<BaseResponse<BlogCategoryResponse>> toggleVisibility(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        
        log.info("PATCH /blog/categories/{}/toggle-visibility", id);
        
        try {
            BlogCategoryResponse response = categoryService.toggleVisibility(id);
            return ResponseUtil.success("Category visibility toggled successfully", response);
        } catch (BaseException e) {
            log.error("Error toggling category visibility: {}", e.getMessage(), e);
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Error toggling category visibility", e);
            return ResponseUtil.internalError("Error toggling category visibility");
        }
    }
}