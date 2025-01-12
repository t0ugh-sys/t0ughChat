package com.forum.service;

import com.forum.dto.request.ArticleRequest;
import com.forum.dto.response.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    ArticleResponse createArticle(Long userId, ArticleRequest articleRequest);
    ArticleResponse updateArticle(Long articleId, Long userId, ArticleRequest articleRequest);
    void deleteArticle(Long articleId, Long userId);
    ArticleResponse getArticle(Long articleId);
    Page<ArticleResponse> getAllArticles(Pageable pageable);
    Page<ArticleResponse> getArticlesByUser(Long userId, Pageable pageable);
    Page<ArticleResponse> getArticlesByCategory(Long categoryId, Pageable pageable);
    Page<ArticleResponse> searchArticles(String keyword, Pageable pageable);
    ArticleResponse incrementViewCount(Long articleId);
    ArticleResponse toggleLike(Long articleId, Long userId);
} 