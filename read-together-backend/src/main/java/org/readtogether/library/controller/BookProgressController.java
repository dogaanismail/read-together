package org.readtogether.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.model.response.BookProgressResponse;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.library.service.BookProgressService;
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
    public CustomResponse<BookProgressResponse> updateProgress(
            @PathVariable UUID bookId,
            @Valid @RequestBody BookProgressUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookProgressResponse response = bookProgressService.createOrUpdateProgress(userId, bookId, request);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<BookProgressResponse> getBookProgress(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookProgressResponse response = bookProgressService.getUserBookProgress(userId, bookId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/my-progress")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookProgressResponse>> getUserProgress(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getUserBookProgress(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/currently-reading")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookProgressResponse>> getCurrentlyReadingBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getCurrentlyReadingBooks(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookProgressResponse>> getCompletedBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getCompletedBooks(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookProgressResponse>> getFavoriteBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getFavoriteBooks(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookProgressResponse>> getRecentlyReadBooks(
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookProgressResponse> response = bookProgressService.getRecentlyReadBooks(userId, days);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/stats/reading-time")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getTotalReadingTime(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Long totalTime = bookProgressService.getTotalReadingTime(userId);

        return CustomResponse.successOf(totalTime);
    }

    @GetMapping("/stats/completed-count")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getCompletedBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookProgressService.getCompletedBooksCount(userId);

        return CustomResponse.successOf(count);
    }

    @GetMapping("/stats/in-progress-count")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getInProgressBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookProgressService.getInProgressBooksCount(userId);

        return CustomResponse.successOf(count);
    }

    @GetMapping("/stats/average-progress")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Double> getAverageProgress(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Double averageProgress = bookProgressService.getAverageProgress(userId);

        return CustomResponse.successOf(averageProgress);
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Void> deleteProgress(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        bookProgressService.deleteProgress(userId, bookId);

        return CustomResponse.SUCCESS;
    }
}
