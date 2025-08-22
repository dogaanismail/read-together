package org.readtogether.library.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book_session")
@Table(name = "book_sessions")
public class BookSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pages_read")
    private Integer pagesRead;

    @Column(name = "reading_time_seconds")
    private Integer readingTimeSeconds;

    @Column(name = "start_page")
    private Integer startPage;

    @Column(name = "end_page")
    private Integer endPage;

    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;

    @Column(name = "difficulty_rating")
    private Integer difficultyRating;

    @Column(name = "comprehension_rating")
    private Integer comprehensionRating;

}
