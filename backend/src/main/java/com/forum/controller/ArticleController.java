package com.forum.controller;

import com.forum.dto.request.ArticleRequest;
import com.forum.dto.response.ArticleResponse;
import com.forum.security.service.UserDetailsImpl;
import com.forum.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ArticleResponse> createArticle(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody ArticleRequest articleRequest) {
        return ResponseEntity.ok(articleService.createArticle(currentUser.getId(), articleRequest));
    }

    @PutMapping("/{articleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody ArticleRequest articleRequest) {
        return ResponseEntity.ok(articleService.updateArticle(articleId, currentUser.getId(), articleRequest));
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        articleService.deleteArticle(articleId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.getArticle(articleId));
    }

    @GetMapping
    public ResponseEntity<Page<ArticleResponse>> getAllArticles(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByUser(userId, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> searchArticles(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(articleService.searchArticles(keyword, pageable));
    }

    @PostMapping("/{articleId}/view")
    public ResponseEntity<ArticleResponse> incrementViewCount(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.incrementViewCount(articleId));
    }

    @PostMapping("/{articleId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ArticleResponse> toggleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(articleService.toggleLike(articleId, currentUser.getId()));
    }
} 