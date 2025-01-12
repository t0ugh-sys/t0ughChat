package com.forum.dto.response;

import com.forum.entity.Tag;
import lombok.Data;

@Data
public class TagResponse {
    private Long id;
    private String name;
    private Integer articleCount;

    public static TagResponse fromTag(Tag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setArticleCount(tag.getArticles() != null ? tag.getArticles().size() : 0);
        return response;
    }
} 