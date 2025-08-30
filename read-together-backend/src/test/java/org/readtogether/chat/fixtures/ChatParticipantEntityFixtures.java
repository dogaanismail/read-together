package org.readtogether.chat.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.common.enums.ParticipantRole;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class ChatParticipantEntityFixtures {

    public static final UUID DEFAULT_PARTICIPANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440200");
    public static final UUID SECONDARY_PARTICIPANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440201");

    public static ChatParticipantEntity createAdmin(
            UUID roomId,
            UUID userId) {

        return createParticipant(
                DEFAULT_PARTICIPANT_ID,
                roomId,
                userId,
                ParticipantRole.ADMIN,
                0
        );
    }

    public static ChatParticipantEntity createMember(
            UUID roomId,
            UUID userId) {

        return createParticipant(
                SECONDARY_PARTICIPANT_ID,
                roomId,
                userId,
                ParticipantRole.MEMBER,
                0
        );
    }

    public static ChatParticipantEntity createParticipant(
            UUID id,
            UUID roomId,
            UUID userId,
            ParticipantRole role,
            int unreadCount) {

        return ChatParticipantEntity.builder()
                .id(id)
                .chatRoomId(roomId)
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .isActive(true)
                .unreadCount(unreadCount)
                .lastReadAt(Instant.now())
                .build();
    }

    public static ChatParticipantEntity createParticipantWithUnread(
            UUID roomId,
            UUID userId,
            ParticipantRole role,
            int unreadCount) {

        return ChatParticipantEntity.builder()
                .chatRoomId(roomId)
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .isActive(true)
                .unreadCount(unreadCount)
                .lastReadAt(Instant.now().minusSeconds(3600)) // 1 hour ago
                .build();
    }
}