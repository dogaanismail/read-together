package org.readtogether.feed.repository;

import org.readtogether.feed.entity.FeedLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLikeEntity, UUID> {

    boolean existsByFeedItemIdAndUserId(UUID feedItemId, UUID userId);

    Optional<FeedLikeEntity> findByFeedItemIdAndUserId(UUID feedItemId, UUID userId);

    long countByFeedItemId(UUID feedItemId);

    Page<FeedLikeEntity> findByFeedItemIdOrderByCreatedAtDesc(UUID feedItemId, Pageable pageable);

    @Query("SELECT COUNT(fl) FROM feedLike fl WHERE fl.feedItemId = :feedItemId")
    long countLikesByFeedItemId(@Param("feedItemId") UUID feedItemId);
}