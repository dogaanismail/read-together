package org.readtogether.feed.repository;

import org.readtogether.feed.entity.FeedItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface FeedRepository extends JpaRepository<FeedItemEntity, UUID> {

    Page<FeedItemEntity> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<FeedItemEntity> findByItemTypeAndIsPublicTrueOrderByCreatedAtDesc(
        FeedItemEntity.FeedItemType itemType, Pageable pageable);

    Page<FeedItemEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<FeedItemEntity> findByCreatedAtAfterAndIsPublicTrueOrderByCreatedAtDesc(
        Instant date, Pageable pageable);

    @Query("SELECT f FROM feedItem f WHERE f.isPublic = true AND " +
           "f.createdAt > :since AND (f.likeCount + f.commentCount + f.viewCount) > :minEngagement " +
           "ORDER BY (f.likeCount + f.commentCount + f.viewCount) DESC")
    Page<FeedItemEntity> findTrendingItems(@Param("since") Instant since,
                                         @Param("minEngagement") Long minEngagement,
                                         Pageable pageable);

    @Modifying
    @Query("UPDATE feedItem f SET f.viewCount = f.viewCount + 1 WHERE f.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE feedItem f SET f.likeCount = f.likeCount + 1 WHERE f.id = :id")
    void incrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE feedItem f SET f.likeCount = f.likeCount - 1 WHERE f.id = :id AND f.likeCount > 0")
    void decrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE feedItem f SET f.commentCount = f.commentCount + 1 WHERE f.id = :id")
    void incrementCommentCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE feedItem f SET f.commentCount = f.commentCount - 1 WHERE f.id = :id AND f.commentCount > 0")
    void decrementCommentCount(@Param("id") UUID id);
}
