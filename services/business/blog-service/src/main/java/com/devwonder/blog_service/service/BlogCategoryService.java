package com.devwonder.blog_service.service;

import com.devwonder.blog_service.dto.BlogCategoryRequest;
import com.devwonder.blog_service.dto.BlogCategoryResponse;
import com.devwonder.blog_service.entity.BlogCategory;
import com.devwonder.blog_service.mapper.BlogMapper;
import com.devwonder.blog_service.repository.BlogCategoryRepository;
import com.devwonder.common.exception.BusinessException;
import com.devwonder.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogCategoryService {
    
    private final BlogCategoryRepository categoryRepository;
    private final BlogMapper blogMapper;
    
    // Get all visible categories
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-categories", key = "'all-visible'")
    public List<BlogCategoryResponse> getAllVisibleCategories() {
        log.info("Fetching all visible categories");
        
        List<BlogCategory> categories = categoryRepository.findVisibleCategories();
        return categories.stream()
            .map(blogMapper::toCategoryResponse)
            .collect(Collectors.toList());
    }
    
    // Get visible categories with pagination
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-categories-paginated", key = "'page:' + #page + ':size:' + #size")
    public Page<BlogCategoryResponse> getVisibleCategories(int page, int size) {
        log.info("Fetching visible categories - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogCategory> categories = categoryRepository.findVisibleCategories(pageable);
        
        return categories.map(blogMapper::toCategoryResponse);
    }
    
    // Get all categories (including invisible - for admin)
    @Transactional(readOnly = true)
    public Page<BlogCategoryResponse> getAllCategories(int page, int size) {
        log.info("Fetching all categories - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogCategory> categories = categoryRepository.findAll(pageable);
        
        return categories.map(blogMapper::toCategoryResponse);
    }
    
    // Get category by slug
    @Transactional(readOnly = true)
    @Cacheable(value = "blog-categories-by-slug", key = "'slug:' + #slug")
    public Optional<BlogCategoryResponse> getCategoryBySlug(String slug) {
        log.info("Fetching category by slug: {}", slug);
        
        return categoryRepository.findVisibleBySlug(slug)
            .map(blogMapper::toCategoryResponse);
    }
    
    // Get category by ID
    @Transactional(readOnly = true)
    public Optional<BlogCategoryResponse> getCategoryById(Long id) {
        log.info("Fetching category by ID: {}", id);
        
        return categoryRepository.findById(id)
            .map(blogMapper::toCategoryResponse);
    }
    
    // Create new category
    @CacheEvict(value = {"blog-categories", "blog-categories-paginated", "blog-categories-by-slug"}, allEntries = true)
    public BlogCategoryResponse createCategory(BlogCategoryRequest request) {
        log.info("Creating new blog category: {}", request.getName());
        
        // Check if slug already exists
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new ValidationException("Category with slug '" + request.getSlug() + "' already exists");
        }
        
        BlogCategory category = blogMapper.toCategoryEntity(request);
        BlogCategory savedCategory = categoryRepository.save(category);
        
        log.info("Blog category created successfully with ID: {}", savedCategory.getId());
        return blogMapper.toCategoryResponse(savedCategory);
    }
    
    // Update category
    @CacheEvict(value = {"blog-categories", "blog-categories-paginated", "blog-categories-by-slug"}, allEntries = true)
    public BlogCategoryResponse updateCategory(Long id, BlogCategoryRequest request) {
        log.info("Updating blog category with ID: {}", id);
        
        BlogCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Category not found with id: " + id));
        
        // Check if slug already exists (excluding current category)
        if (!category.getSlug().equals(request.getSlug()) && categoryRepository.existsBySlug(request.getSlug())) {
            throw new ValidationException("Category with slug '" + request.getSlug() + "' already exists");
        }
        
        // Update category fields
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setIsVisible(request.getIsVisible());
        
        BlogCategory updatedCategory = categoryRepository.save(category);
        log.info("Blog category updated successfully: {}", updatedCategory.getName());
        
        return blogMapper.toCategoryResponse(updatedCategory);
    }
    
    // Delete category
    @CacheEvict(value = {"blog-categories", "blog-categories-paginated", "blog-categories-by-slug"}, allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Deleting blog category with ID: {}", id);
        
        BlogCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Category not found with id: " + id));
        
        // Check if category has posts
        if (category.getPostsCount() > 0) {
            throw new ValidationException("Cannot delete category with existing posts. Please move posts to another category first.");
        }
        
        categoryRepository.delete(category);
        log.info("Blog category deleted successfully: {}", category.getName());
    }
    
    // Toggle visibility
    @CacheEvict(value = {"blog-categories", "blog-categories-paginated", "blog-categories-by-slug"}, allEntries = true)
    public BlogCategoryResponse toggleVisibility(Long id) {
        log.info("Toggling visibility for blog category with ID: {}", id);
        
        BlogCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Category not found with id: " + id));
        
        category.setIsVisible(!category.getIsVisible());
        BlogCategory updatedCategory = categoryRepository.save(category);
        
        log.info("Blog category visibility toggled: {} - visible: {}", updatedCategory.getName(), updatedCategory.getIsVisible());
        return blogMapper.toCategoryResponse(updatedCategory);
    }
}