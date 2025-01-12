package com.forum.service.impl;

import com.forum.dto.request.TagRequest;
import com.forum.dto.response.TagResponse;
import com.forum.entity.Tag;
import com.forum.repository.TagRepository;
import com.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public TagResponse createTag(TagRequest tagRequest) {
        if (tagRepository.existsByName(tagRequest.getName())) {
            throw new RuntimeException("标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setName(tagRequest.getName());

        return TagResponse.fromTag(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public TagResponse updateTag(Long tagId, TagRequest tagRequest) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("标签不存在"));

        if (!tag.getName().equals(tagRequest.getName()) && 
            tagRepository.existsByName(tagRequest.getName())) {
            throw new RuntimeException("标签名称已存在");
        }

        tag.setName(tagRequest.getName());
        return TagResponse.fromTag(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("标签不存在"));

        if (tag.getArticles() != null && !tag.getArticles().isEmpty()) {
            throw new RuntimeException("该标签下还有文章，无法删除");
        }

        tagRepository.delete(tag);
    }

    @Override
    public TagResponse getTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("标签不存在"));
        return TagResponse.fromTag(tag);
    }

    @Override
    public Page<TagResponse> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(TagResponse::fromTag);
    }

    @Override
    public List<TagResponse> getPopularTags(int limit) {
        // 这里简化处理，实际应该根据文章数量排序
        return tagRepository.findAll().stream()
                .sorted((t1, t2) -> {
                    int count1 = t1.getArticles() != null ? t1.getArticles().size() : 0;
                    int count2 = t2.getArticles() != null ? t2.getArticles().size() : 0;
                    return count2 - count1;
                })
                .limit(limit)
                .map(TagResponse::fromTag)
                .collect(Collectors.toList());
    }

    @Override
    public Set<TagResponse> getTagsByNames(Set<String> tagNames) {
        List<Tag> tags = tagRepository.findByNameIn(new ArrayList<>(tagNames));
        return tags.stream()
                .map(TagResponse::fromTag)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
} 