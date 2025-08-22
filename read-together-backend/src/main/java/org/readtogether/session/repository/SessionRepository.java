package org.readtogether.session.repository;

import org.readtogether.session.common.enums.MediaType;
import org.readtogether.session.common.enums.ProcessingStatus;
import org.readtogether.session.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    Page<SessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<SessionEntity> findByIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
            ProcessingStatus status, Pageable pageable);

    Page<SessionEntity> findByMediaTypeAndIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
            MediaType mediaType, ProcessingStatus status, Pageable pageable);

    @Query("SELECT s FROM sessions s WHERE " +
            "(LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "s.isPublic = true AND s.processingStatus = :status " +
            "ORDER BY s.createdAt DESC")
    Page<SessionEntity> searchPublicSessions(@Param("query") String query,
                                             @Param("status") ProcessingStatus status,
                                             Pageable pageable);

    Optional<SessionEntity> findByIdAndUserId(UUID id, UUID userId);
}
