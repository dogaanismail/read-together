package org.readtogether.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookCreateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.createBook(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID bookId,
            @Valid @RequestBody BookUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.updateBook(bookId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> deleteBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        bookService.deleteBook(bookId, userId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.getBookById(bookId, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookResponse>> getUserBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookResponse> response = bookService.getUserBooks(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-books/paged")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Page<BookResponse>> getUserBooksPaged(
            Authentication authentication,
            Pageable pageable) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Page<BookResponse> response = bookService.getUserBooks(userId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BookResponse>> getPublicBooks() {

        List<BookResponse> response = bookService.getPublicBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/paged")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Page<BookResponse>> getPublicBooksPaged(
            Pageable pageable) {

        Page<BookResponse> response = bookService.getPublicBooks(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/my-books")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookResponse>> searchUserBooks(
            @RequestParam String query,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookResponse> response = bookService.searchUserBooks(userId, query);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/public")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<BookResponse>> searchPublicBooks(
            @RequestParam String query) {

        List<BookResponse> response = bookService.searchPublicBooks(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-books/count")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Long> getUserBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookService.getUserBooksCount(userId);

        return ResponseEntity.ok(count);
    }

    @GetMapping("/public/count")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Long> getPublicBooksCount() {

        long count = bookService.getPublicBooksCount();
        return ResponseEntity.ok(count);
    }
}
