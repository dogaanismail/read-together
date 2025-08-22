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
@Entity(name = "feedComment")
@Table(name = "feed_comments",
       indexes = {
           @Index(name = "idx_feed_comments_feed_item_created", columnList = "feed_item_id, created_at"),
           @Index(name = "idx_feed_comments_parent", columnList = "parent_comment_id")
       })
public class FeedCommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "feed_item_id", nullable = false)
    private UUID feedItemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_item_id", insertable = false, updatable = false)
    private FeedItemEntity feedItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", insertable = false, updatable = false)
    private FeedCommentEntity parentComment;
}