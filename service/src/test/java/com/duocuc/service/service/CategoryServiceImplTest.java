package com.duocuc.service.service;

import com.duocuc.service.dto.CategoryRequest;
import com.duocuc.service.dto.CategoryResponse;
import com.duocuc.service.entity.Category;
import com.duocuc.service.exception.BusinessRuleException;
import com.duocuc.service.exception.DuplicateResourceException;
import com.duocuc.service.exception.ResourceNotFoundException;
import com.duocuc.service.repository.CategoryRepository;
import com.duocuc.service.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Diagnostics");
        category.setDescription("General checkup");
        category.setActive(true);
    }

    @Test
    @DisplayName("save: creates a category when the name does not exist")
    void save_shouldCreate_whenNameDoesNotExist() {
        // Given
        CategoryRequest request = new CategoryRequest("Diagnostics", "General checkup");
        when(categoryRepository.existsByNameIgnoreCase("Diagnostics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryResponse response = categoryService.save(request);

        // Then
        assertEquals("Diagnostics", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the name already exists")
    void save_shouldThrowException_whenNameIsDuplicated() {
        // Given
        CategoryRequest request = new CategoryRequest("Diagnostics", null);
        when(categoryRepository.existsByNameIgnoreCase("Diagnostics")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> categoryService.save(request));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(99L));
    }

    @Test
    @DisplayName("findAll: returns the list of categories")
    void findAll_shouldReturnList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // When
        List<CategoryResponse> list = categoryService.findAll();

        // Then
        assertEquals(1, list.size());
        assertEquals("Diagnostics", list.get(0).name());
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the category is inactive")
    void update_shouldThrowException_whenInactive() {
        // Given
        category.setActive(false);
        CategoryRequest request = new CategoryRequest("Advanced Diagnostics", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> categoryService.update(1L, request));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("deactivate: marks the category as inactive (logical delete)")
    void deactivate_shouldDeactivate() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        categoryService.deactivate(1L);

        // Then
        assertFalse(category.getActive());
        verify(categoryRepository).save(category);
    }
}
