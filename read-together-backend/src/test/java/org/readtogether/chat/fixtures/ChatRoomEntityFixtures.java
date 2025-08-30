package org.readtogether.chat.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.common.enums.ChatRoomType;

import java.util.UUID;

@UtilityClass
public class ChatRoomEntityFixtures {

    public static final UUID DEFAULT_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    public static final UUID SECONDARY_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440101");
    public static final UUID DEFAULT_CREATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public static ChatRoomEntity createDefaultRoom() {

        return createRoom(
                DEFAULT_ROOM_ID,
                "Test Chat Room",
                DEFAULT_CREATOR_ID,
                ChatRoomType.GROUP,
                10
        );
    }

    public static ChatRoomEntity createDirectChatRoom() {

        return createRoom(
                SECONDARY_ROOM_ID,
                "Direct Chat",
                DEFAULT_CREATOR_ID,
                ChatRoomType.DIRECT,
                2
        );
    }

    public static ChatRoomEntity createRoom(
            UUID id,
            String name,
            UUID creatorId,
            ChatRoomType type,
            Integer maxParticipants) {
        
        return ChatRoomEntity.builder()
                .id(id)
                .name(name)
                .description("Test room description")
                .type(type)
                .creatorId(creatorId)
                .isActive(true)
                .maxParticipants(maxParticipants)
                .build();
    }

    public static ChatRoomEntity createRoom(
            String name,
            UUID creatorId,
            ChatRoomType type) {
        
        return ChatRoomEntity.builder()
                .name(name)
                .description("Test room description")
                .type(type)
                .creatorId(creatorId)
                .isActive(true)
                .maxParticipants(10)
                .build();
    }

}