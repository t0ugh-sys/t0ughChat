package com.forum.service.impl;

import com.forum.dto.request.CommentRequest;
import com.forum.dto.response.CommentResponse;
import com.forum.entity.Article;
import com.forum.entity.Comment;
import com.forum.entity.User;
import com.forum.repository.ArticleRepository;
import com.forum.repository.CommentRepository;
import com.forum.repository.UserRepository;
import com.forum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, CommentRequest commentRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        Article article = articleRepository.findById(commentRequest.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setArticle(article);

        if (commentRequest.getParentId() != null) {
            Comment parent = commentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("父评论不存在"));
            comment.setParent(parent);
        }

        return CommentResponse.fromComment(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("评论不存在"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("没有权限修改此评论");
        }

        comment.setContent(content);
        return CommentResponse.fromComment(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("评论不存在"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("没有权限删除此评论");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommentResponse getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("评论不存在"));
        return CommentResponse.fromComment(comment);
    }

    @Override
    public List<CommentResponse> getArticleComments(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        
        return commentRepository.findByArticleAndParentIsNull(article).stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getUserComments(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        
        return commentRepository.findByUser(user, pageable)
                .map(CommentResponse::fromComment);
    }

    @Override
    @Transactional
    public CommentResponse toggleLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("评论不存在"));
        
        // 这里简化处理，实际应该有一个点赞关联表
        comment.setLikeCount(comment.getLikeCount() + 1);
        return CommentResponse.fromComment(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponse> getReplies(Long commentId) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("评论不存在"));
        
        return commentRepository.findByParent(parent).stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }
} 