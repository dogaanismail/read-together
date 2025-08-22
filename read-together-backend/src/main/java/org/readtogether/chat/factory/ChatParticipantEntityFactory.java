package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.common.enums.ParticipantRole;

import java.time.Instant;
import java.util.UUID;

import static org.readtogether.chat.common.enums.ParticipantRole.ADMIN;
import static org.readtogether.chat.common.enums.ParticipantRole.MEMBER;

@UtilityClass
public class ChatParticipantEntityFactory {

    public static ChatParticipantEntity createParticipant(
            UUID chatRoomId,
            UUID userId,
            ParticipantRole role) {

        return ChatParticipantEntity.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .isActive(true)
                .unreadCount(0)
                .lastReadAt(Instant.now())
                .build();
    }

    public static ChatParticipantEntity createMember(
            UUID chatRoomId,
            UUID userId) {

        return createParticipant(chatRoomId, userId, MEMBER);
    }

    public static ChatParticipantEntity createAdmin(
            UUID chatRoomId,
            UUID userId) {

        return createParticipant(chatRoomId, userId, ADMIN);
    }
}