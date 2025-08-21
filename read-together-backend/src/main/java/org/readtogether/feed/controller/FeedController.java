package org.readtogether.feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

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
                FeedItemEntity.FeedItemType itemType = type != null ?
                    FeedItemEntity.FeedItemType.valueOf(type.toUpperCase()) : null;
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
    public ResponseEntity<Void> likeFeedItem(@PathVariable UUID id) {

        feedService.likeFeedItem(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeFeedItem(@PathVariable UUID id) {

        feedService.unlikeFeedItem(id);
        return ResponseEntity.ok().build();
    }
}
