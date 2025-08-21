package org.readtogether.session.repository;

import org.readtogether.session.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    Page<SessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<SessionEntity> findByIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
        SessionEntity.ProcessingStatus status, Pageable pageable);

    Page<SessionEntity> findByMediaTypeAndIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
        SessionEntity.MediaType mediaType, SessionEntity.ProcessingStatus status, Pageable pageable);

    List<SessionEntity> findByReadingRoomIdOrderByCreatedAtDesc(UUID readingRoomId);

    List<SessionEntity> findByProcessingStatus(SessionEntity.ProcessingStatus status);

    Page<SessionEntity> findByCreatedAtAfterAndIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
        Instant date, SessionEntity.ProcessingStatus status, Pageable pageable);

    @Query("SELECT s FROM session s WHERE " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "s.isPublic = true AND s.processingStatus = :status " +
           "ORDER BY s.createdAt DESC")
    Page<SessionEntity> searchPublicSessions(@Param("query") String query,
                                           @Param("status") SessionEntity.ProcessingStatus status,
                                           Pageable pageable);

    long countByUserId(UUID userId);

    Optional<SessionEntity> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("UPDATE sessions s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE sessions s SET s.likeCount = s.likeCount + 1 WHERE s.id = :id")
    void incrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE sessions s SET s.likeCount = s.likeCount - 1 WHERE s.id = :id AND s.likeCount > 0")
    void decrementLikeCount(@Param("id") UUID id);
}
