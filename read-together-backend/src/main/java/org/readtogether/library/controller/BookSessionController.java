package org.readtogether.library.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.entity.BookSessionEntity;
import org.readtogether.library.service.BookSessionService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookSessionEntity> createBookSession(
            @RequestParam UUID sessionId,
            @RequestParam UUID bookId,
            @RequestParam(required = false) Integer pagesRead,
            @RequestParam(required = false) Integer readingTimeSeconds,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookSessionEntity response = bookSessionService.createBookSession(sessionId,
                bookId,
                userId,
                pagesRead,
                readingTimeSeconds
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<BookSessionEntity> updateBookSession(
            @PathVariable UUID sessionId,
            @RequestParam(required = false) Integer pagesRead,
            @RequestParam(required = false) Integer readingTimeSeconds,
            @RequestParam(required = false) String sessionNotes,
            @RequestParam(required = false) Integer difficultyRating,
            @RequestParam(required = false) Integer comprehensionRating) {

        BookSessionEntity response = bookSessionService.updateBookSession(sessionId,
                pagesRead,
                readingTimeSeconds,
                sessionNotes,
                difficultyRating,
                comprehensionRating
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookSessionEntity>> getBookSessions(
            @PathVariable UUID bookId) {

        List<BookSessionEntity> response = bookSessionService.getBookSessions(bookId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookSessionEntity>> getUserSessions(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookSessionEntity> response = bookSessionService.getUserSessions(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/book/{bookId}")
    public ResponseEntity<List<BookSessionEntity>> getUserBookSessions(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookSessionEntity> response = bookSessionService.getUserBookSessions(userId, bookId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/recent")
    public ResponseEntity<List<BookSessionEntity>> getRecentUserSessions(
            @RequestParam(defaultValue = "30") int days,
            @RequestHeader("User-ID") UUID userId) {

        List<BookSessionEntity> response = bookSessionService.getRecentUserSessions(userId, days);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/reading-time/{bookId}")
    public ResponseEntity<Long> getTotalReadingTimeForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Long totalTime = bookSessionService.getTotalReadingTimeForUserBook(userId, bookId);

        return ResponseEntity.ok(totalTime != null ? totalTime : 0L);
    }

    @GetMapping("/stats/pages-read/{bookId}")
    public ResponseEntity<Integer> getTotalPagesReadForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Integer totalPages = bookSessionService.getTotalPagesReadForUserBook(userId, bookId);

        return ResponseEntity.ok(totalPages != null ? totalPages : 0);
    }

    @GetMapping("/stats/session-count/{bookId}")
    public ResponseEntity<Long> getSessionCountForBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookSessionService.getSessionCountForUserBook(userId, bookId);

        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteBookSession(
            @PathVariable UUID sessionId) {

        bookSessionService.deleteBookSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/exists")
    public ResponseEntity<Boolean> sessionExists(
            @PathVariable UUID sessionId) {

        boolean exists = bookSessionService.existsBySessionId(sessionId);
        return ResponseEntity.ok(exists);
    }
}
