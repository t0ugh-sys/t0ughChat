package com.forum.controller;

import com.forum.dto.request.CommentRequest;
import com.forum.dto.response.CommentResponse;
import com.forum.security.service.UserDetailsImpl;
import com.forum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @Valid @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.createComment(currentUser.getId(), commentRequest));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody String content) {
        return ResponseEntity.ok(commentService.updateComment(commentId, currentUser.getId(), content));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        commentService.deleteComment(commentId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentResponse>> getArticleComments(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.getArticleComments(articleId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentResponse>> getUserComments(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(commentService.getUserComments(userId, pageable));
    }

    @PostMapping("/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponse> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(commentService.toggleLike(commentId, currentUser.getId()));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }
} 