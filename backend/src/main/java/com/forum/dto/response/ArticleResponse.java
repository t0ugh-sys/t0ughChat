package com.forum.dto.response;

import com.forum.entity.Article;
import com.forum.entity.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private UserResponse author;
    private String category;
    private Set<String> tags;
    private Integer viewCount;
    private Integer likeCount;
    private ArticleStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static ArticleResponse fromArticle(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setContent(article.getContent());
        response.setAuthor(UserResponse.fromUser(article.getAuthor()));
        response.setCategory(article.getCategory().getName());
        response.setTags(article.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toSet()));
        response.setViewCount(article.getViewCount());
        response.setLikeCount(article.getLikeCount());
        response.setStatus(article.getStatus());
        response.setCreateTime(article.getCreateTime());
        response.setUpdateTime(article.getUpdateTime());
        return response;
    }
} 