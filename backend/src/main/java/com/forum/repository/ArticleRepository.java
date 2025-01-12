package com.forum.repository;

import com.forum.entity.Article;
import com.forum.entity.ArticleStatus;
import com.forum.entity.Category;
import com.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);
    Page<Article> findByAuthor(User author, Pageable pageable);
    Page<Article> findByCategory(Category category, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword%")
    Page<Article> searchByKeyword(String keyword, Pageable pageable);
    
    List<Article> findTop10ByOrderByViewCountDesc();
    List<Article> findTop10ByOrderByCreateTimeDesc();
} 