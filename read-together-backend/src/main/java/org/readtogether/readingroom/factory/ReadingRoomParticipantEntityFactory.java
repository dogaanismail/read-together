package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.readtogether.user.entity.UserEntity;

import java.time.Instant;

import static org.readtogether.readingroom.common.enums.ApprovalStatus.APPROVED;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.JOINED;
import static org.readtogether.readingroom.common.enums.ParticipantStatus.LEFT;

@UtilityClass
public class ReadingRoomParticipantEntityFactory {

    public ReadingRoomParticipantEntity createParticipant(
            ReadingRoomEntity readingRoom,
            UserEntity user,
            boolean autoMuteNewJoiners) {

        return ReadingRoomParticipantEntity.builder()
                .readingRoom(readingRoom)
                .user(user)
                .status(JOINED)
                .joinedAt(Instant.now())
                .isMuted(autoMuteNewJoiners)
                .isVideoEnabled(true)
                .isSpeaking(false)
                .approvalStatus(APPROVED)
                .build();
    }

    public ReadingRoomParticipantEntity participantLeft(
            ReadingRoomParticipantEntity participant) {

        participant.setStatus(LEFT);
        participant.setLeftAt(Instant.now());
        return participant;
    }
}
