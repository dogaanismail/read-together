package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.ApprovalStatus;
import org.readtogether.readingroom.common.enums.ParticipantStatus;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.readtogether.user.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.readtogether.readingroom.common.enums.ApprovalStatus.*;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.*;

@UtilityClass
public class ReadingRoomParticipantEntityFixtures {

    public static ReadingRoomParticipantEntity createJoinedParticipant(
            ReadingRoomEntity room,
            UserEntity user) {

        return createParticipantEntity(
                room,
                user,
                JOINED,
                Instant.now().minus(5, MINUTES),
                null,
                false,
                true,
                false,
                APPROVED,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createLeftParticipant(
            ReadingRoomEntity room,
            UserEntity user) {

        return createParticipantEntity(
                room,
                user,
                LEFT,
                Instant.now().minus(1, HOURS),
                Instant.now().minus(10, MINUTES),
                false,
                false,
                false,
                APPROVED,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createPendingParticipant(
            ReadingRoomEntity room,
            UserEntity user) {

        return createParticipantEntity(
                room,
                user,
                INVITED,
                Instant.now().minus(2, MINUTES),
                null,
                false,
                true,
                false,
                PENDING,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createMutedParticipant(
            ReadingRoomEntity room,
            UserEntity user) {

        return createParticipantEntity(
                room,
                user,
                JOINED,
                Instant.now().minus(10, MINUTES),
                null,
                true,
                false,
                false,
                APPROVED,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createSpeakingParticipant(
            ReadingRoomEntity room,
            UserEntity user) {

        return createParticipantEntity(
                room,
                user,
                JOINED,
                Instant.now().minus(3, MINUTES),
                null,
                false,
                true,
                true,
                APPROVED,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createParticipantWithAutoMute(
            ReadingRoomEntity room,
            UserEntity user,
            boolean autoMuted) {

        return createParticipantEntity(
                room,
                user,
                JOINED,
                Instant.now(),
                null,
                autoMuted,
                true,
                false,
                APPROVED,
                null,
                null
        );
    }

    public static ReadingRoomParticipantEntity createParticipantEntity(
            ReadingRoomEntity room,
            UserEntity user,
            ParticipantStatus status,
            Instant joinedAt,
            Instant leftAt,
            boolean isMuted,
            boolean isVideoEnabled,
            boolean isSpeaking,
            ApprovalStatus approvalStatus,
            UUID approvedBy,
            Instant approvedAt) {

        return ReadingRoomParticipantEntity.builder()
                .id(UUID.randomUUID())
                .readingRoom(room)
                .user(user)
                .status(status)
                .joinedAt(joinedAt)
                .leftAt(leftAt)
                .isMuted(isMuted)
                .isVideoEnabled(isVideoEnabled)
                .isSpeaking(isSpeaking)
                .approvalStatus(approvalStatus)
                .approvedBy(approvedBy)
                .approvedAt(approvedAt)
                .build();
    }

    public static ReadingRoomParticipantEntity markParticipantLeft(
            ReadingRoomParticipantEntity participant) {

        participant.setStatus(LEFT);
        participant.setLeftAt(Instant.now());
        participant.setSpeaking(false);
        return participant;
    }
}