package com.devwonder.product_service.service;

import com.devwonder.product_service.entity.Category;
import com.devwonder.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Category createCategory(Category category) {
        log.info("Creating category: {}", category.getName());
        
        // Validation
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Validation
        if (categoryDetails.getName() == null || categoryDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updatedCategory.getName());
        return updatedCategory;
    }

    public void deleteCategory(Long id) {
        log.info("Hard deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        categoryRepository.delete(category);
        log.info("Category hard deleted successfully: {}", category.getName());
    }

    public void softDeleteCategory(Long id) {
        log.info("Soft deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (category.getDeletedAt() != null) {
            throw new IllegalStateException("Category is already deleted");
        }

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
        log.info("Category soft deleted successfully: {}", category.getName());
    }

    public void restoreCategory(Long id) {
        log.info("Restoring category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (category.getDeletedAt() == null) {
            throw new IllegalStateException("Category is not deleted");
        }

        category.setDeletedAt(null);
        categoryRepository.save(category);
        log.info("Category restored successfully: {}", category.getName());
    }

    @Transactional(readOnly = true)
    public Page<Category> getAllActiveCategories(int page, int size) {
        log.info("Fetching all active categories - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findActiveById(Long id) {
        return categoryRepository.findActiveById(id);
    }
}