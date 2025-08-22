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
@Entity(name = "feedLike")
@Table(name = "feed_likes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"feed_item_id", "user_id"}),
       indexes = @Index(name = "idx_feed_likes_feed_item_id", columnList = "feed_item_id"))
public class FeedLikeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "feed_item_id", nullable = false)
    private UUID feedItemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_item_id", insertable = false, updatable = false)
    private FeedItemEntity feedItem;
}