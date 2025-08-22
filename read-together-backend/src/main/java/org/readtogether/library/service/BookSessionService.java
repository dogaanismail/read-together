package org.readtogether.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.library.entity.BookSessionEntity;
import org.readtogether.library.factory.BookSessionFactory;
import org.readtogether.library.repository.BookSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookSessionService {

    private final BookSessionRepository bookSessionRepository;
    private final BookProgressService bookProgressService;

    @Transactional
    public BookSessionEntity createBookSession(
            UUID sessionId,
            UUID bookId,
            UUID userId,
            Integer pagesRead,
            Integer readingTimeSeconds) {

        BookSessionEntity bookSession = BookSessionFactory.createBookSessionEntity(
                sessionId,
                bookId,
                userId,
                pagesRead,
                readingTimeSeconds
        );

        BookSessionEntity savedSession = bookSessionRepository.save(bookSession);

        bookProgressService.updateProgressFromSession(userId, bookId, pagesRead, readingTimeSeconds);

        log.info("Book session created for session: {}, book: {}, user: {}", sessionId, bookId, userId);
        return savedSession;
    }

    @Transactional
    public BookSessionEntity updateBookSession(
            UUID sessionId,
            Integer pagesRead,
            Integer readingTimeSeconds,
            String sessionNotes,
            Integer difficultyRating,
            Integer comprehensionRating) {

        List<BookSessionEntity> sessions = bookSessionRepository.findBySessionId(sessionId);

        if (sessions.isEmpty()) {
            throw new RuntimeException("Book session not found for session ID: " + sessionId);
        }

        BookSessionEntity bookSession = sessions.getFirst();

        updateSessionDetails(
                bookSession,
                pagesRead, readingTimeSeconds,
                sessionNotes,
                difficultyRating,
                comprehensionRating
        );

        BookSessionEntity savedSession = bookSessionRepository.save(bookSession);

        if (pagesRead != null || readingTimeSeconds != null) {

            bookProgressService.updateProgressFromSession(
                    bookSession.getUserId(),
                    bookSession.getBookId(),
                    pagesRead,
                    readingTimeSeconds);
        }

        log.info("Book session updated for session: {}", sessionId);
        return savedSession;
    }

    public List<BookSessionEntity> getBookSessions(
            UUID bookId) {

        return bookSessionRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    public List<BookSessionEntity> getUserSessions(
            UUID userId) {

        return bookSessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<BookSessionEntity> getUserBookSessions(
            UUID userId,
            UUID bookId) {

        return bookSessionRepository.findByUserIdAndBookIdOrderByCreatedAtDesc(userId, bookId);
    }

    public List<BookSessionEntity> getRecentUserSessions(
            UUID userId,
            int days) {

        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return bookSessionRepository.findRecentSessionsByUserId(userId, sinceDate);
    }

    public Long getTotalReadingTimeForUserBook(
            UUID userId,
            UUID bookId) {

        return bookSessionRepository.getTotalReadingTimeForUserBook(userId, bookId);
    }

    public Integer getTotalPagesReadForUserBook(
            UUID userId,
            UUID bookId) {

        return bookSessionRepository.getTotalPagesReadForUserBook(userId, bookId);
    }

    public long getSessionCountForUserBook(
            UUID userId,
            UUID bookId) {

        return bookSessionRepository.countSessionsForUserBook(userId, bookId);
    }

    @Transactional
    public void deleteBookSession(
            UUID sessionId) {

        bookSessionRepository.deleteBySessionId(sessionId);
        log.info("Book session deleted for session: {}", sessionId);
    }

    public boolean existsBySessionId(
            UUID sessionId) {

        return bookSessionRepository.existsBySessionId(sessionId);
    }

    private void updateSessionDetails(
            BookSessionEntity bookSession,
            Integer pagesRead,
            Integer readingTimeSeconds,
            String sessionNotes,
            Integer difficultyRating,
            Integer comprehensionRating) {

        if (pagesRead != null) {
            bookSession.setPagesRead(pagesRead);
        }

        if (readingTimeSeconds != null) {
            bookSession.setReadingTimeSeconds(readingTimeSeconds);
        }

        if (sessionNotes != null) {
            bookSession.setSessionNotes(sessionNotes);
        }

        if (difficultyRating != null) {
            bookSession.setDifficultyRating(difficultyRating);
        }

        if (comprehensionRating != null) {
            bookSession.setComprehensionRating(comprehensionRating);
        }
    }
}
