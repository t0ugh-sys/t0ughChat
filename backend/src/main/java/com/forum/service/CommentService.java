package com.forum.service;

import com.forum.dto.request.CommentRequest;
import com.forum.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long userId, CommentRequest commentRequest);
    CommentResponse updateComment(Long commentId, Long userId, String content);
    void deleteComment(Long commentId, Long userId);
    CommentResponse getComment(Long commentId);
    List<CommentResponse> getArticleComments(Long articleId);
    Page<CommentResponse> getUserComments(Long userId, Pageable pageable);
    CommentResponse toggleLike(Long commentId, Long userId);
    List<CommentResponse> getReplies(Long commentId);
} 