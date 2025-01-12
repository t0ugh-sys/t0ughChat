package com.forum.controller;

import com.forum.dto.request.TagRequest;
import com.forum.dto.response.TagResponse;
import com.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagRequest) {
        return ResponseEntity.ok(tagService.createTag(tagRequest));
    }

    @PutMapping("/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody TagRequest tagRequest) {
        return ResponseEntity.ok(tagService.updateTag(tagId, tagRequest));
    }

    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> getTag(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getTag(tagId));
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TagResponse>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(tagService.getPopularTags(limit));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<TagResponse>> getTagsByNames(@RequestBody Set<String> tagNames) {
        return ResponseEntity.ok(tagService.getTagsByNames(tagNames));
    }

    @GetMapping("/check-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> checkNameExists(@RequestParam String name) {
        return ResponseEntity.ok(tagService.existsByName(name));
    }
} 