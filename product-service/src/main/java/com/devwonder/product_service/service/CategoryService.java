package com.devwonder.product_service.service;

import com.devwonder.product_service.entity.Category;
import com.devwonder.product_service.repository.CategoryRepository;
import com.devwonder.product_service.exception.CategoryNotFoundException;
import com.devwonder.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @CacheEvict(value = {"categories", "categories-active", "products-by-category"}, allEntries = true)
    public Category createCategory(Category category) {
        log.info("Creating category: {}", category.getName());
        
        // Validation
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching all categories from database");
        return categoryRepository.findAll();
    }

    @Cacheable(value = "category-detail", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        log.info("Fetching category detail from database for ID: {}", id);
        return categoryRepository.findById(id);
    }

    @CacheEvict(value = {"categories", "categories-active", "category-detail", "products-by-category"}, allEntries = true)
    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        // Validation
        if (categoryDetails.getName() == null || categoryDetails.getName().trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updatedCategory.getName());
        return updatedCategory;
    }

    @CacheEvict(value = {"categories", "categories-active", "category-detail", "products-by-category"}, allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Hard deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(category);
        log.info("Category hard deleted successfully: {}", category.getName());
    }

    @CacheEvict(value = {"categories", "categories-active", "category-detail", "products-by-category"}, allEntries = true)
    public void softDeleteCategory(Long id) {
        log.info("Soft deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        if (category.getDeletedAt() != null) {
            throw new ValidationException("Category is already deleted");
        }

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
        log.info("Category soft deleted successfully: {}", category.getName());
    }

    @CacheEvict(value = {"categories", "categories-active", "category-detail", "products-by-category"}, allEntries = true)
    public void restoreCategory(Long id) {
        log.info("Restoring category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        if (category.getDeletedAt() == null) {
            throw new ValidationException("Category is not deleted");
        }

        category.setDeletedAt(null);
        categoryRepository.save(category);
        log.info("Category restored successfully: {}", category.getName());
    }

    @Cacheable(value = "categories-active", key = "'active:page:' + #page + ':size:' + #size")
    @Transactional(readOnly = true)
    public Page<Category> getAllActiveCategories(int page, int size) {
        log.info("Fetching all active categories from database - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAllActive(pageable);
    }

    @Cacheable(value = "category-detail", key = "'active:' + #id")
    @Transactional(readOnly = true)
    public Optional<Category> findActiveById(Long id) {
        log.info("Fetching active category detail from database for ID: {}", id);
        return categoryRepository.findActiveById(id);
    }
}