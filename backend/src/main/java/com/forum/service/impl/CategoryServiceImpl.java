package com.forum.service.impl;

import com.forum.dto.request.CategoryRequest;
import com.forum.dto.response.CategoryResponse;
import com.forum.entity.Category;
import com.forum.repository.CategoryRepository;
import com.forum.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new RuntimeException("分类名称已存在");
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        return CategoryResponse.fromCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));

        if (!category.getName().equals(categoryRequest.getName()) && 
            categoryRepository.existsByName(categoryRequest.getName())) {
            throw new RuntimeException("分类名称已存在");
        }

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        return CategoryResponse.fromCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));

        if (category.getArticles() != null && !category.getArticles().isEmpty()) {
            throw new RuntimeException("该分类下还有文章，无法删除");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));
        return CategoryResponse.fromCategory(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
} 