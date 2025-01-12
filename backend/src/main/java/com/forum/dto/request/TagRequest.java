package com.forum.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class TagRequest {
    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 20, message = "标签名称长度必须在1-20之间")
    @Pattern(regexp = "^[\\w\\u4e00-\\u9fa5]+$", message = "标签名称只能包含字母、数字、下划线和中文")
    private String name;
} 