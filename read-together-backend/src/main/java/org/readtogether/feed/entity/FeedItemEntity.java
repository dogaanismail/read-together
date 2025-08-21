package org.readtogether.feed.entity;

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
@Entity(name = "feedItem")
@Table(name = "feed_items")
public class FeedItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private FeedItemType itemType;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "view_count")
    @Builder.Default
    private long viewCount = 0L;

    @Column(name = "like_count")
    @Builder.Default
    private long likeCount = 0L;

    @Column(name = "comment_count")
    @Builder.Default
    private long commentCount = 0L;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    public enum FeedItemType {
        SESSION,
        ACHIEVEMENT,
        MILESTONE,
        ROOM_JOIN,
        STREAK
    }
}
