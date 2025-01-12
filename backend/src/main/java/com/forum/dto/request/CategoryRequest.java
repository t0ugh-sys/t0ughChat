package com.forum.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 2, max = 50, message = "分类名称长度必须在2-50之间")
    private String name;

    @Size(max = 200, message = "分类描述不能超过200个字符")
    private String description;
} 