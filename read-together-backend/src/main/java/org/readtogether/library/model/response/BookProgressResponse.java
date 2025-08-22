package org.readtogether.library.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.library.common.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookProgressResponse {

    private UUID id;
    private UUID userId;
    private UUID bookId;
    private BookStatus status;
    private Integer currentPage;
    private Integer progressPercentage;
    private Integer totalSessionsCompleted;
    private Long totalReadingTimeSeconds;
    private LocalDateTime startedReadingAt;
    private LocalDateTime lastReadAt;
    private LocalDateTime completedAt;
    private String notes;
    private String favoriteQuotes;
    private Integer personalRating;
    private Integer readingGoalPagesPerDay;
    private boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BookResponse book;
}
