package com.duocuc.service.service.impl;

import com.duocuc.service.dto.CategoryRequest;
import com.duocuc.service.dto.CategoryResponse;
import com.duocuc.service.entity.Category;
import com.duocuc.service.exception.BusinessRuleException;
import com.duocuc.service.exception.DuplicateResourceException;
import com.duocuc.service.exception.ResourceNotFoundException;
import com.duocuc.service.repository.CategoryRepository;
import com.duocuc.service.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse save(CategoryRequest request) {
        // Business rule: the category name must be unique
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A category already exists with name: " + request.name());
        }
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        Category saved = categoryRepository.save(category);
        log.info("Category created with id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<CategoryResponse> findAll() {
        log.info("Listing all categories");
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return toResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (Boolean.FALSE.equals(category.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive category (id: " + id + ")");
        }
        if (!category.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A category already exists with name: " + request.name());
        }
        category.setName(request.name());
        category.setDescription(request.description());
        Category updated = categoryRepository.save(category);
        log.info("Category updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category deactivated id={}", id);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getActive());
    }
}
