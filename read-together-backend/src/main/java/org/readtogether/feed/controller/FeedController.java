package org.readtogether.feed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.exception.BadRequestException;
import org.readtogether.common.exception.RecordNotFoundException;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.feed.common.enums.FeedItemType;
import org.readtogether.feed.model.CreateCommentRequest;
import org.readtogether.feed.model.FeedCommentResponse;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.service.FeedCommentService;
import org.readtogether.feed.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public CustomResponse<Page<FeedItemResponse>> getFeed(
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
                throw new BadRequestException("Invalid feed item type: " + type);
            }
        } else {
            feedItems = feedService.getFeed(pageable);
        }

        return CustomResponse.successOf(feedItems);
    }

    @GetMapping("/trending")
    public CustomResponse<Page<FeedItemResponse>> getTrendingFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemResponse> feedItems = feedService.getTrendingFeed(pageable);

        return CustomResponse.successOf(feedItems);
    }

    @GetMapping("/user/{userId}")
    public CustomResponse<Page<FeedItemResponse>> getUserFeed(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedItemResponse> feedItems = feedService.getUserFeed(userId, pageable);

        return CustomResponse.successOf(feedItems);
    }

    @PostMapping("/{id}/view")
    public CustomResponse<Void> incrementViewCount(@PathVariable UUID id) {

        feedService.incrementViewCount(id);
        return CustomResponse.SUCCESS;
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Void> likeFeedItem(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        boolean success = feedService.likeFeedItem(id, userId);
        if (!success) {
            throw new BadRequestException("Unable to like feed item: " + id);
        }
        return CustomResponse.SUCCESS;
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Void> unlikeFeedItem(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        boolean success = feedService.unlikeFeedItem(id, userId);
        if (!success) {
            throw new BadRequestException("Unable to unlike feed item: " + id);
        }
        
        return CustomResponse.SUCCESS;
    }

    @GetMapping("/{id}/comments")
    public CustomResponse<Page<FeedCommentResponse>> getComments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedCommentResponse> comments = feedCommentService.getComments(id, pageable);

        return CustomResponse.successOf(comments);
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<FeedCommentResponse> createComment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        FeedCommentResponse comment = feedCommentService.createComment(id, userId, request);

        return CustomResponse.successOf(comment);
    }

    @GetMapping("/comments/{commentId}/replies")
    public CustomResponse<Page<FeedCommentResponse>> getReplies(
            @PathVariable UUID commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedCommentResponse> replies = feedCommentService.getReplies(commentId, pageable);

        return CustomResponse.successOf(replies);
    }

    @DeleteMapping("/comments/{commentId}")
    public CustomResponse<Void> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        boolean success = feedCommentService.deleteComment(commentId, userId);
        if (!success) {
            throw new RecordNotFoundException("Comment not found or not owned by user: " + commentId);
        }

        return CustomResponse.SUCCESS;
    }
}
