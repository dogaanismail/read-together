package org.readtogether.chat.entity;

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
@Entity(name = "chat_participants")
@Table(
    name = "chat_participants",
    uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
)
public class ChatParticipantEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "chat_room_id", nullable = false)
    private UUID chatRoomId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private java.time.Instant joinedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "unread_count", nullable = false)
    @Builder.Default
    private Integer unreadCount = 0;

    @Column(name = "last_read_at")
    private java.time.Instant lastReadAt;

    public enum ParticipantRole {
        ADMIN,
        MODERATOR,
        MEMBER
    }
}