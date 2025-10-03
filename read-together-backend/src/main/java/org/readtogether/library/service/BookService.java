package org.readtogether.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.library.entity.BookEntity;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.library.repository.BookRepository;
import org.readtogether.library.repository.BookProgressRepository;
import org.readtogether.library.factory.BookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookProgressRepository bookProgressRepository;

    @Transactional
    public BookResponse createBook(
            BookCreateRequest request,
            UUID userId) {

        validateBookCreation(request, userId);

        BookEntity bookEntity = BookFactory.createBookEntity(request, userId);
        BookEntity savedBook = bookRepository.save(bookEntity);

        log.info("Book created successfully with ID: {} by user: {}", savedBook.getId(), userId);
        return BookFactory.createBookResponse(savedBook);
    }

    @Transactional
    public BookResponse updateBook(
            UUID bookId,
            BookUpdateRequest request,
            UUID userId) {

        BookEntity bookEntity = findBookByIdAndUserId(bookId, userId);
        BookFactory.updateBookEntity(bookEntity, request);

        BookEntity savedBook = bookRepository.save(bookEntity);
        log.info("Book updated successfully with ID: {} by user: {}", bookId, userId);

        return BookFactory.createBookResponse(savedBook);
    }

    @Transactional
    public void deleteBook(
            UUID bookId,
            UUID userId) {

        BookEntity bookEntity = findBookByIdAndUserId(bookId, userId);

        bookProgressRepository.deleteByUserIdAndBookId(userId, bookId);
        bookRepository.delete(bookEntity);

        log.info("Book deleted successfully with ID: {} by user: {}", bookId, userId);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(
            UUID bookId,
            UUID userId) {

        BookEntity bookEntity = findBookByIdAndUserId(bookId, userId);
        return BookFactory.createBookResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getUserBooks(
            UUID userId) {

        List<BookEntity> books = bookRepository.findByAddedByUserIdOrderByCreatedAtDesc(userId);
        return BookFactory.createBookResponses(books);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getUserBooks(
            UUID userId,
            Pageable pageable) {

        Page<BookEntity> books = bookRepository.findByAddedByUserIdOrderByCreatedAtDesc(userId, pageable);
        return books.map(BookFactory::createBookResponse);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getPublicBooks() {

        List<BookEntity> books = bookRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        return BookFactory.createBookResponses(books);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getPublicBooks(
            Pageable pageable) {

        Page<BookEntity> books = bookRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
        return books.map(BookFactory::createBookResponse);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchUserBooks(
            UUID userId,
            String searchTerm) {

        List<BookEntity> books = bookRepository.findByUserIdAndSearchTerm(userId, searchTerm);
        return BookFactory.createBookResponses(books);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchPublicBooks(
            String searchTerm) {

        List<BookEntity> books = bookRepository.findPublicBooksBySearchTerm(searchTerm);
        return BookFactory.createBookResponses(books);
    }

    @Transactional(readOnly = true)
    public long getUserBooksCount(
            UUID userId) {

        return bookRepository.countByAddedByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getPublicBooksCount() {

        return bookRepository.countPublicBooks();
    }


    private BookEntity findBookByIdAndUserId(
            UUID bookId,
            UUID userId) {

        return bookRepository.findByIdAndAddedByUserId(bookId, userId)
                .orElseThrow(() -> new RuntimeException("Book not found or access denied"));
    }

    private void validateBookCreation(
            BookCreateRequest request,
            UUID userId) {

        if (request.getIsbn() != null &&
                bookRepository.existsByIsbnAndAddedByUserId(request.getIsbn(), userId)) {
            throw new RuntimeException("Book with this ISBN already exists in your library");
        }
    }
}
