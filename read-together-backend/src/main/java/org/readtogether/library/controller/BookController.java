package org.readtogether.library.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CustomResponse<BookResponse> createBook(
            @Valid @RequestBody BookCreateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.createBook(request, userId);

        return CustomResponse.createdOf(response);
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<BookResponse> updateBook(
            @PathVariable UUID bookId,
            @Valid @RequestBody BookUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.updateBook(bookId, request, userId);

        return CustomResponse.successOf(response);
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        bookService.deleteBook(bookId, userId);
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<BookResponse> getBook(
            @PathVariable UUID bookId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        BookResponse response = bookService.getBookById(bookId, userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookResponse>> getUserBooks(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookResponse> response = bookService.getUserBooks(userId);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/my-books/paged")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Page<BookResponse>> getUserBooksPaged(
            Authentication authentication,
            Pageable pageable) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Page<BookResponse> response = bookService.getUserBooks(userId, pageable);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/public")
    @PermitAll
    public CustomResponse<List<BookResponse>> getPublicBooks() {

        List<BookResponse> response = bookService.getPublicBooks();

        return CustomResponse.successOf(response);
    }

    @GetMapping("/public/paged")
    @PermitAll
    public CustomResponse<Page<BookResponse>> getPublicBooksPaged(
            Pageable pageable) {

        Page<BookResponse> response = bookService.getPublicBooks(pageable);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/search/my-books")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<List<BookResponse>> searchUserBooks(
            @RequestParam String query,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        List<BookResponse> response = bookService.searchUserBooks(userId, query);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/search/public")
    @PermitAll
    public CustomResponse<List<BookResponse>> searchPublicBooks(
            @RequestParam String query) {

        List<BookResponse> response = bookService.searchPublicBooks(query);

        return CustomResponse.successOf(response);
    }

    @GetMapping("/my-books/count")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Long> getUserBooksCount(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = bookService.getUserBooksCount(userId);

        return CustomResponse.successOf(count);
    }

    @GetMapping("/public/count")
    @PermitAll
    public CustomResponse<Long> getPublicBooksCount() {

        long count = bookService.getPublicBooksCount();
        return CustomResponse.successOf(count);
    }
}
