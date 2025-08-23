package org.readtogether.library.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.library.common.enums.BookStatus;

import java.time.Instant;
import java.util.UUID;

import static org.readtogether.library.common.enums.BookStatus.NOT_STARTED;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book_progress")
@Table(name = "book_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class BookProgressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BookStatus status = NOT_STARTED;

    @Column(name = "current_page")
    @Builder.Default
    private int currentPage = 0;

    @Column(name = "progress_percentage")
    @Builder.Default
    private int progressPercentage = 0;

    @Column(name = "total_sessions_completed")
    @Builder.Default
    private int totalSessionsCompleted = 0;

    @Column(name = "total_reading_time_seconds")
    @Builder.Default
    private long totalReadingTimeSeconds = 0L;

    @Column(name = "started_reading_at")
    private Instant startedReadingAt;

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "favorite_quotes", columnDefinition = "TEXT")
    private String favoriteQuotes;

    @Column(name = "personal_rating")
    private Integer personalRating;

    @Column(name = "reading_goal_pages_per_day")
    private Integer readingGoalPagesPerDay;

    @Column(name = "is_favorite")
    @Builder.Default
    private boolean isFavorite = false;

}
