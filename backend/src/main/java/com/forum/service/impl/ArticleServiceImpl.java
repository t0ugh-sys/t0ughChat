package com.forum.service.impl;

import com.forum.dto.request.ArticleRequest;
import com.forum.dto.response.ArticleResponse;
import com.forum.entity.*;
import com.forum.repository.ArticleRepository;
import com.forum.repository.CategoryRepository;
import com.forum.repository.TagRepository;
import com.forum.repository.UserRepository;
import com.forum.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public ArticleResponse createArticle(Long userId, ArticleRequest articleRequest) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        Category category = categoryRepository.findById(articleRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));

        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setAuthor(author);
        article.setCategory(category);
        article.setStatus(ArticleStatus.PUBLISHED);

        if (articleRequest.getTags() != null && !articleRequest.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : articleRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            article.setTags(tags);
        }

        return ArticleResponse.fromArticle(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleResponse updateArticle(Long articleId, Long userId, ArticleRequest articleRequest) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));

        if (!article.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("没有权限修改此文章");
        }

        Category category = categoryRepository.findById(articleRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));

        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setCategory(category);

        if (articleRequest.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : articleRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            article.setTags(tags);
        }

        return ArticleResponse.fromArticle(articleRepository.save(article));
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));

        if (!article.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("没有权限删除此文章");
        }

        articleRepository.delete(article);
    }

    @Override
    public ArticleResponse getArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        return ArticleResponse.fromArticle(article);
    }

    @Override
    public Page<ArticleResponse> getAllArticles(Pageable pageable) {
        return articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable)
                .map(ArticleResponse::fromArticle);
    }

    @Override
    public Page<ArticleResponse> getArticlesByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        return articleRepository.findByAuthor(user, pageable)
                .map(ArticleResponse::fromArticle);
    }

    @Override
    public Page<ArticleResponse> getArticlesByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));
        return articleRepository.findByCategory(category, pageable)
                .map(ArticleResponse::fromArticle);
    }

    @Override
    public Page<ArticleResponse> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchByKeyword(keyword, pageable)
                .map(ArticleResponse::fromArticle);
    }

    @Override
    @Transactional
    public ArticleResponse incrementViewCount(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        article.setViewCount(article.getViewCount() + 1);
        return ArticleResponse.fromArticle(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleResponse toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        
        // 这里简化处理，实际应该有一个点赞关联表
        article.setLikeCount(article.getLikeCount() + 1);
        return ArticleResponse.fromArticle(articleRepository.save(article));
    }
} 