package org.readtogether.readingroom.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.readingroom.common.enums.ApprovalStatus;
import org.readtogether.readingroom.common.enums.ParticipantStatus;
import org.readtogether.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.ApprovalStatus.APPROVED;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.JOINED;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reading_room_participant")
public class ReadingRoomParticipantEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_room_id", nullable = false)
    private ReadingRoomEntity readingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ParticipantStatus status = JOINED;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_muted")
    @Builder.Default
    private boolean isMuted = false;

    @Column(name = "is_video_enabled")
    @Builder.Default
    private boolean isVideoEnabled = true;

    @Column(name = "is_speaking")
    @Builder.Default
    private boolean isSpeaking = false;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApprovalStatus approvalStatus = APPROVED;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
