package com.forum.dto.response;

import com.forum.entity.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Integer articleCount;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setArticleCount(category.getArticles() != null ? category.getArticles().size() : 0);
        return response;
    }
} 