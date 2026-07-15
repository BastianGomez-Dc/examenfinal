package com.duocuc.service.service;

import com.duocuc.service.dto.CategoryRequest;
import com.duocuc.service.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse save(CategoryRequest request);

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    void deactivate(Long id);
}
