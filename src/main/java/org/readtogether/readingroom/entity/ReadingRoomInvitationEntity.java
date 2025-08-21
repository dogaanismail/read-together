package org.readtogether.readingroom.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.readingroom.common.enums.InvitationStatus;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reading_room_invitation")
public class ReadingRoomInvitationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_room_id", nullable = false)
    private ReadingRoomEntity readingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    private UserEntity invitedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private UserEntity invitedUser;

    @Column(name = "invited_email")
    private String invitedEmail;

    @Column(name = "invitation_token", unique = true, nullable = false)
    private String invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_type")
    private InvitationType invitationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "declined_at")
    private LocalDateTime declinedAt;

    @Column(name = "message")
    private String message;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
