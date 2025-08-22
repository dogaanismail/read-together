package org.readtogether.library.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.library.common.enums.BookCategory;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book")
@Table(name = "books")
public class BookEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "author", nullable = false, length = 200)
    private String author;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BookCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "estimated_reading_minutes")
    private Integer estimatedReadingMinutes;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "en";

    @Column(name = "publisher", length = 200)
    private String publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "added_by_user_id", nullable = false)
    private UUID addedByUserId;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = false;

    @Column(name = "difficulty_level")
    private Integer difficultyLevel;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "external_source", length = 50)
    private String externalSource;

}
