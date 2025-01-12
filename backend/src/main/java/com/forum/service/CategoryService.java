package com.forum.service;

import com.forum.dto.request.CategoryRequest;
import com.forum.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
    void deleteCategory(Long categoryId);
    CategoryResponse getCategory(Long categoryId);
    List<CategoryResponse> getAllCategories();
    boolean existsByName(String name);
} 