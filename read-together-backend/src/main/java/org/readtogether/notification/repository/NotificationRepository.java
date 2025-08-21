package org.readtogether.notification.repository;

import org.readtogether.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndIsReadOrderByCreatedAtDesc(UUID userId, boolean isRead, Pageable pageable);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") UUID userId);

    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :notificationId AND n.userId = :userId")
    int markAsRead(@Param("notificationId") UUID notificationId, @Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    @Query("SELECT n FROM NotificationEntity n WHERE n.sessionId = :sessionId ORDER BY n.createdAt DESC")
    List<NotificationEntity> findBySessionIdOrderByCreatedAtDesc(@Param("sessionId") UUID sessionId);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
