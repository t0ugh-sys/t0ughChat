package com.forum.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class ArticleRequest {
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Set<String> tags;
} 