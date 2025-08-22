package org.readtogether.library.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.library.common.enums.BookStatus;
import org.readtogether.library.entity.BookProgressEntity;
import org.readtogether.library.model.response.BookProgressResponse;
import org.readtogether.library.model.request.BookProgressUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class BookProgressFactory {

    public static BookProgressEntity createProgressEntity(
            UUID userId,
            UUID bookId) {

        return BookProgressEntity.builder()
                .userId(userId)
                .bookId(bookId)
                .status(BookStatus.NOT_STARTED)
                .currentPage(0)
                .progressPercentage(0)
                .totalSessionsCompleted(0)
                .totalReadingTimeSeconds(0L)
                .isFavorite(false)
                .build();
    }

    public static void updateProgressEntity(
            BookProgressEntity entity,
            BookProgressUpdateRequest request) {

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        if (request.getCurrentPage() != null) {
            entity.setCurrentPage(request.getCurrentPage());
        }

        if (request.getProgressPercentage() != null) {
            entity.setProgressPercentage(request.getProgressPercentage());
        }

        if (request.getNotes() != null) {
            entity.setNotes(request.getNotes());
        }

        if (request.getFavoriteQuotes() != null) {
            entity.setFavoriteQuotes(request.getFavoriteQuotes());
        }

        if (request.getPersonalRating() != null) {
            entity.setPersonalRating(request.getPersonalRating());
        }

        if (request.getReadingGoalPagesPerDay() != null) {
            entity.setReadingGoalPagesPerDay(request.getReadingGoalPagesPerDay());
        }

        if (request.getIsFavorite() != null) {
            entity.setFavorite(request.getIsFavorite());
        }
    }

    public static BookProgressResponse createProgressResponse(
            BookProgressEntity entity) {

        BookProgressResponse.BookProgressResponseBuilder builder = BookProgressResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bookId(entity.getBookId())
                .status(entity.getStatus())
                .currentPage(entity.getCurrentPage())
                .progressPercentage(entity.getProgressPercentage())
                .totalSessionsCompleted(entity.getTotalSessionsCompleted())
                .totalReadingTimeSeconds(entity.getTotalReadingTimeSeconds())
                .startedReadingAt(entity.getStartedReadingAt())
                .lastReadAt(entity.getLastReadAt())
                .completedAt(entity.getCompletedAt())
                .notes(entity.getNotes())
                .favoriteQuotes(entity.getFavoriteQuotes())
                .personalRating(entity.getPersonalRating())
                .readingGoalPagesPerDay(entity.getReadingGoalPagesPerDay())
                .isFavorite(entity.isFavorite())
                .createdAt(LocalDateTime.from(entity.getCreatedAt()))
                .updatedAt(LocalDateTime.from(entity.getUpdatedAt()));

        return builder.build();
    }

    public static List<BookProgressResponse> createProgressResponses(
            List<BookProgressEntity> entities) {

        return entities.stream()
                .map(BookProgressFactory::createProgressResponse)
                .toList();
    }
}
