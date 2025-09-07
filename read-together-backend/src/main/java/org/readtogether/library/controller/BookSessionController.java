package org.readtogether.library.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.entity.BookSessionEntity;
import org.readtogether.library.service.BookSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/sessions")
@RequiredArgsConstructor
public class BookSessionController {

    private final BookSessionService bookSessionService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<BookSessionEntity> createBookSession(
            @RequestParam UUID sessionId,
            @RequestParam UUID bookId,
            @RequestParam(required = false) Integer pagesRead,
            @RequestParam(required = false) Integer readingTimeSeconds,
            Authentication authentication) {

        //TODO: We have to fix here, no exposing entity, have to use dto
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookSessionEntity response = bookSessionService.createBookSession(sessionId,
                bookId,
                userId,
                pagesRead,
                readingTimeSeconds
        );

        return CustomResponse.successOf(response);
    }

    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<BookSessionEntity> updateBookSession(
            @PathVariable UUID sessionId,
            @RequestParam(required = false) Integer pagesRead,
            @RequestParam(required = false) Integer readingTimeSeconds,
            @RequestParam(required = false) String sessionNotes,
            @RequestParam(required = false) Integer difficultyRating,
            @RequestParam(required = false) Integer comprehensionRating) {

        //TODO: We have to fix here, no exposing entity, have to use dto
        BookSessionEntity response = bookSessionService.updateBookSession(sessionId,
                pagesRead,
                readingTimeSeconds,
                sessionNotes,
                difficultyRating,
                comprehensionRating
        );

        return CustomResponse.successOf(response);
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookSessionEntity>> getBookSessions(
            @PathVariable UUID bookId) {

        //TODO: We have to fix here, no exposing entity, have to use dto
        List<BookSessionEntity> response = bookSessionService.getBookSessions(bookId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookSessionEntity>> getUserSessions(
            Authentication authentication) {

        //TODO: We have to fix here, no exposing entity, have to use dto
        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookSessionEntity> response = bookSessionService.getUserSessions(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/user/book/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookSessionEntity>> getUserBookSessions(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookSessionEntity> response = bookSessionService.getUserBookSessions(userId, bookId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/user/recent")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookSessionEntity>> getRecentUserSessions(
            @RequestParam(defaultValue = "30") int days,
            @RequestHeader("User-ID") UUID userId) {

        //TODO: We have to fix here, no exposing entity, have to use dto
        List<BookSessionEntity> response = bookSessionService.getRecentUserSessions(userId, days);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/stats/reading-time/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getTotalReadingTimeForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Long totalTime = bookSessionService.getTotalReadingTimeForUserBook(userId, bookId);

        return CustomResponse.successOf(totalTime != null ? totalTime : 0L);
    }

    @GetMapping("/stats/pages-read/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Integer> getTotalPagesReadForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Integer totalPages = bookSessionService.getTotalPagesReadForUserBook(userId, bookId);

        return CustomResponse.successOf(totalPages != null ? totalPages : 0);
    }

    @GetMapping("/stats/session-count/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getSessionCountForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookSessionService.getSessionCountForUserBook(userId, bookId);

        return CustomResponse.successOf(count);
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Void> deleteBookSession(
            @PathVariable UUID sessionId) {

        bookSessionService.deleteBookSession(sessionId);

        return CustomResponse.SUCCESS;
    }

    @GetMapping("/{sessionId}/exists")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Boolean> sessionExists(
            @PathVariable UUID sessionId) {

        boolean exists = bookSessionService.existsBySessionId(sessionId);

        return CustomResponse.successOf(exists);
    }
}
