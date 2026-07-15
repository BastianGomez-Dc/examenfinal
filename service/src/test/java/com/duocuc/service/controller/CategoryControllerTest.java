package com.duocuc.service.controller;

import com.duocuc.service.dto.CategoryRequest;
import com.duocuc.service.dto.CategoryResponse;
import com.duocuc.service.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse(1L, "Diagnostics", "General checkup", true);
    }

    @Test
    @DisplayName("create: returns 201 with the created category")
    void create_shouldReturn201() {
        // Given
        CategoryRequest request = new CategoryRequest("Diagnostics", "General checkup");
        when(categoryService.save(any(CategoryRequest.class))).thenReturn(categoryResponse);

        // When
        ResponseEntity<CategoryResponse> result = categoryController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Diagnostics", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of categories")
    void findAll_shouldReturn200() {
        // Given
        when(categoryService.findAll()).thenReturn(List.of(categoryResponse));

        // When
        ResponseEntity<List<CategoryResponse>> result = categoryController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found category")
    void findById_shouldReturn200() {
        // Given
        when(categoryService.findById(1L)).thenReturn(categoryResponse);

        // When
        ResponseEntity<CategoryResponse> result = categoryController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = categoryController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
