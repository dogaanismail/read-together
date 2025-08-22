package org.readtogether.feed.repository;

import org.readtogether.feed.entity.FeedCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedCommentEntity, UUID> {

    Page<FeedCommentEntity> findByFeedItemIdAndIsDeletedFalseOrderByCreatedAtAsc(UUID feedItemId, Pageable pageable);

    Page<FeedCommentEntity> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(UUID parentCommentId, Pageable pageable);

    long countByFeedItemIdAndIsDeletedFalse(UUID feedItemId);

    @Query("SELECT COUNT(fc) FROM feedComment fc WHERE fc.feedItemId = :feedItemId AND fc.isDeleted = false")
    long countCommentsByFeedItemId(@Param("feedItemId") UUID feedItemId);

    @Query("SELECT fc FROM feedComment fc WHERE fc.feedItemId = :feedItemId AND fc.parentCommentId IS NULL AND fc.isDeleted = false ORDER BY fc.createdAt ASC")
    Page<FeedCommentEntity> findTopLevelCommentsByFeedItemId(@Param("feedItemId") UUID feedItemId, Pageable pageable);
}