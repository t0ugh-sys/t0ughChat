package com.forum.repository;

import com.forum.entity.Article;
import com.forum.entity.Comment;
import com.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByArticle(Article article, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);
    List<Comment> findByArticleAndParentIsNull(Article article);
    List<Comment> findByParent(Comment parent);
} 