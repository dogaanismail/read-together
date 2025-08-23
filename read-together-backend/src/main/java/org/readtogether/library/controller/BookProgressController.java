package org.readtogether.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.model.response.BookProgressResponse;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.library.service.BookProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/progress")
@RequiredArgsConstructor
public class BookProgressController {

    private final BookProgressService bookProgressService;

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BookProgressResponse> updateProgress(
            @PathVariable UUID bookId,
            @Valid @RequestBody BookProgressUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookProgressResponse response = bookProgressService.createOrUpdateProgress(userId, bookId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BookProgressResponse> getBookProgress(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookProgressResponse response = bookProgressService.getUserBookProgress(userId, bookId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-progress")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookProgressResponse>> getUserProgress(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getUserBookProgress(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/currently-reading")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookProgressResponse>> getCurrentlyReadingBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getCurrentlyReadingBooks(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookProgressResponse>> getCompletedBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getCompletedBooks(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookProgressResponse>> getFavoriteBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getFavoriteBooks(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookProgressResponse>> getRecentlyReadBooks(
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getRecentlyReadBooks(userId, days);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/reading-time")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Long> getTotalReadingTime(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Long totalTime = bookProgressService.getTotalReadingTime(userId);

        return ResponseEntity.ok(totalTime != null ? totalTime : 0L);
    }

    @GetMapping("/stats/completed-count")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Long> getCompletedBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookProgressService.getCompletedBooksCount(userId);

        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/in-progress-count")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Long> getInProgressBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookProgressService.getInProgressBooksCount(userId);

        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/average-progress")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Double> getAverageProgress(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Double averageProgress = bookProgressService.getAverageProgress(userId);

        return ResponseEntity.ok(averageProgress != null ? averageProgress : 0.0);
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> deleteProgress(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        bookProgressService.deleteProgress(userId, bookId);

        return ResponseEntity.noContent().build();
    }
}
