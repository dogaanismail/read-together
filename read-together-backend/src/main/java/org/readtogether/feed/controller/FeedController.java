package org.readtogether.feed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.feed.entity.enums.FeedItemType;
import org.readtogether.feed.model.CreateCommentRequest;
import org.readtogether.feed.model.FeedCommentResponse;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.service.FeedCommentService;
import org.readtogether.feed.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FeedCommentService feedCommentService;

    @GetMapping
    public ResponseEntity<Page<FeedItemResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemResponse> feedItems;

        if (type != null || search != null || language != null) {
            try {
                FeedItemType itemType = type != null ?
                    FeedItemType.valueOf(type.toUpperCase()) : null;
                feedItems = feedService.getFeedWithFilters(itemType, search, language, sortBy, sortDirection, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid feed item type: {}", type);
                return ResponseEntity.badRequest().build();
            }
        } else {
            feedItems = feedService.getFeed(pageable);
        }

        return ResponseEntity.ok(feedItems);
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<FeedItemResponse>> getTrendingFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemResponse> feedItems = feedService.getTrendingFeed(pageable);

        return ResponseEntity.ok(feedItems);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FeedItemResponse>> getUserFeed(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemResponse> feedItems = feedService.getUserFeed(userId, pageable);

        return ResponseEntity.ok(feedItems);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {

        feedService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeFeedItem(
            @PathVariable UUID id, 
            Authentication authentication) {

        try {
            UUID userId = SecurityUtils.getCurrentUserId(authentication);
            boolean success = feedService.likeFeedItem(id, userId);
            
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error liking feed item {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeFeedItem(
            @PathVariable UUID id, 
            Authentication authentication) {

        try {
            UUID userId = SecurityUtils.getCurrentUserId(authentication);
            boolean success = feedService.unlikeFeedItem(id, userId);
            
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error unliking feed item {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<FeedCommentResponse>> getComments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<FeedCommentResponse> comments = feedCommentService.getComments(id, pageable);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error getting comments for feed item {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<FeedCommentResponse> createComment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        try {
            UUID userId = SecurityUtils.getCurrentUserId(authentication);
            FeedCommentResponse comment = feedCommentService.createComment(id, userId, request);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            log.error("Error creating comment for feed item {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Page<FeedCommentResponse>> getReplies(
            @PathVariable UUID commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<FeedCommentResponse> replies = feedCommentService.getReplies(commentId, pageable);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("Error getting replies for comment {}", commentId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId, 
            Authentication authentication) {

        try {
            UUID userId = SecurityUtils.getCurrentUserId(authentication);
            boolean success = feedCommentService.deleteComment(commentId, userId);
            
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting comment {}", commentId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
