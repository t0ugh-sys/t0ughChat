package com.forum.service;

import com.forum.dto.request.TagRequest;
import com.forum.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface TagService {
    TagResponse createTag(TagRequest tagRequest);
    TagResponse updateTag(Long tagId, TagRequest tagRequest);
    void deleteTag(Long tagId);
    TagResponse getTag(Long tagId);
    Page<TagResponse> getAllTags(Pageable pageable);
    List<TagResponse> getPopularTags(int limit);
    Set<TagResponse> getTagsByNames(Set<String> tagNames);
    boolean existsByName(String name);
} 