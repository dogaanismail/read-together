package org.readtogether.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "notifications")
@Table(name = "notifications")
public class NotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_id")
    private UUID sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public enum NotificationType {
        SESSION_UPLOAD_STARTED,
        SESSION_UPLOAD_PROGRESS,
        SESSION_UPLOAD_COMPLETED,
        SESSION_UPLOAD_FAILED,
        SESSION_PROCESSING_STARTED,
        SESSION_PROCESSING_COMPLETED,
        SESSION_PROCESSING_FAILED,
        GENERAL_INFO,
        SYSTEM_ALERT
    }
}
