package org.readtogether.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.library.entity.BookProgressEntity;
import org.readtogether.library.model.response.BookProgressResponse;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.library.repository.BookProgressRepository;
import org.readtogether.library.factory.BookProgressFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.readtogether.library.common.enums.BookStatus.NOT_STARTED;
import static org.readtogether.library.common.enums.BookStatus.COMPLETED;
import static org.readtogether.library.common.enums.BookStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookProgressService {

    private final BookProgressRepository bookProgressRepository;

    @Transactional
    public BookProgressResponse createOrUpdateProgress(
            UUID userId,
            UUID bookId,
            BookProgressUpdateRequest request) {

        BookProgressEntity progressEntity = bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> BookProgressFactory.createProgressEntity(userId, bookId));

        BookProgressFactory.updateProgressEntity(progressEntity, request);
        updateProgressTimestamps(progressEntity, request);

        BookProgressEntity savedProgress = bookProgressRepository.save(progressEntity);
        log.info("Book progress updated for user: {} and book: {}", userId, bookId);

        return BookProgressFactory.createProgressResponse(savedProgress);
    }

    @Transactional
    public void updateProgressFromSession(
            UUID userId,
            UUID bookId,
            Integer pagesRead,
            Integer readingTimeSeconds) {

        BookProgressEntity progressEntity = bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> BookProgressFactory.createProgressEntity(userId, bookId));

        updateProgressFromSessionData(progressEntity, pagesRead, readingTimeSeconds);

        bookProgressRepository.save(progressEntity);
        log.info("Book progress updated from session for user: {} and book: {}", userId, bookId);
    }

    @Transactional(readOnly = true)
    public BookProgressResponse getUserBookProgress(
            UUID userId,
            UUID bookId) {

        BookProgressEntity progressEntity = bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> BookProgressFactory.createProgressEntity(userId, bookId));

        return BookProgressFactory.createProgressResponse(progressEntity);
    }

    @Transactional(readOnly = true)
    public List<BookProgressResponse> getUserBookProgress(
            UUID userId) {

        List<BookProgressEntity> progressList = bookProgressRepository.findByUserIdOrderByLastReadAtDesc(userId);

        return BookProgressFactory.createProgressResponses(progressList);
    }

    @Transactional(readOnly = true)
    public List<BookProgressResponse> getCurrentlyReadingBooks(
            UUID userId) {

        List<BookProgressEntity> progressList = bookProgressRepository.findCurrentlyReadingByUserId(userId);

        return BookProgressFactory.createProgressResponses(progressList);
    }

    @Transactional(readOnly = true)
    public List<BookProgressResponse> getCompletedBooks(
            UUID userId) {

        List<BookProgressEntity> progressList = bookProgressRepository.findCompletedBooksByUserId(userId);

        return BookProgressFactory.createProgressResponses(progressList);
    }

    @Transactional(readOnly = true)
    public List<BookProgressResponse> getFavoriteBooks(
            UUID userId) {

        List<BookProgressEntity> progressList = bookProgressRepository.findByUserIdAndIsFavoriteTrueOrderByLastReadAtDesc(userId);

        return BookProgressFactory.createProgressResponses(progressList);
    }

    @Transactional(readOnly = true)
    public List<BookProgressResponse> getRecentlyReadBooks(
            UUID userId,
            int days) {

        Instant sinceDate = Instant.now().minus(days, ChronoUnit.DAYS);
        List<BookProgressEntity> progressList = bookProgressRepository.findRecentlyReadBooks(userId, sinceDate);

        return BookProgressFactory.createProgressResponses(progressList);
    }

    @Transactional(readOnly = true)
    public Long getTotalReadingTime(
            UUID userId) {

        return bookProgressRepository.getTotalReadingTimeByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getCompletedBooksCount(
            UUID userId) {

        return bookProgressRepository.countCompletedBooksByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getInProgressBooksCount(
            UUID userId) {

        return bookProgressRepository.countInProgressBooksByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Double getAverageProgress(
            UUID userId) {

        return bookProgressRepository.getAverageProgressByUserId(userId);
    }

    @Transactional
    public void deleteProgress(
            UUID userId,
            UUID bookId) {

        bookProgressRepository.deleteByUserIdAndBookId(userId, bookId);
        log.info("Book progress deleted for user: {} and book: {}", userId, bookId);
    }

    private void updateProgressTimestamps(
            BookProgressEntity progressEntity,
            BookProgressUpdateRequest request) {

        Instant now = Instant.now();

        if (progressEntity.getStartedReadingAt() == null && request.getStatus() == IN_PROGRESS) {
            progressEntity.setStartedReadingAt(now);
        }

        if (request.getStatus() == COMPLETED) {
            progressEntity.setCompletedAt(now);
        }

        progressEntity.setLastReadAt(now);
    }

    private void updateProgressFromSessionData(
            BookProgressEntity progressEntity,
            Integer pagesRead,
            Integer readingTimeSeconds) {

        if (pagesRead != null && pagesRead > 0) {
            int newCurrentPage = progressEntity.getCurrentPage() + pagesRead;
            progressEntity.setCurrentPage(newCurrentPage);
        }

        if (readingTimeSeconds != null && readingTimeSeconds > 0) {
            long newTotalTime = progressEntity.getTotalReadingTimeSeconds() + readingTimeSeconds;
            progressEntity.setTotalReadingTimeSeconds(newTotalTime);
        }

        int newSessionCount = progressEntity.getTotalSessionsCompleted() + 1;
        progressEntity.setTotalSessionsCompleted(newSessionCount);

        if (progressEntity.getStatus() == NOT_STARTED) {
            progressEntity.setStatus(IN_PROGRESS);
            progressEntity.setStartedReadingAt(Instant.now());
        }

        progressEntity.setLastReadAt(Instant.now());
    }
}
