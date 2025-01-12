package com.forum.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentRequest {
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;  // 父评论ID，用于回复评论
} 