package com.forum.dto.response;

import com.forum.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private UserResponse user;
    private Long articleId;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long parentId;
    private List<CommentResponse> replies = new ArrayList<>();

    public static CommentResponse fromComment(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUser(UserResponse.fromUser(comment.getUser()));
        response.setArticleId(comment.getArticle().getId());
        response.setLikeCount(comment.getLikeCount());
        response.setCreateTime(comment.getCreateTime());
        response.setUpdateTime(comment.getUpdateTime());
        
        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }
        
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            response.setReplies(comment.getReplies().stream()
                    .map(CommentResponse::fromComment)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
} 