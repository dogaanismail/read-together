package org.readtogether.chat.factory;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.common.enums.ChatRoomType;
import org.readtogether.chat.fixtures.ChatRequestFixtures;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("ChatRoomEntityFactory Tests")
class ChatRoomEntityFactoryTest {

    private static final UUID TEST_CREATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    @DisplayName("Should create chat room from request")
    void shouldCreateChatRoomFromRequest() {
        // Given
        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();

        // When
        ChatRoomEntity result = ChatRoomEntityFactory.createFromRequest(request, TEST_CREATOR_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Room");
        assertThat(result.getDescription()).isEqualTo("A test chat room");
        assertThat(result.getType()).isEqualTo(ChatRoomType.GROUP);
        assertThat(result.getCreatorId()).isEqualTo(TEST_CREATOR_ID);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getMaxParticipants()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should create direct chat room")
    void shouldCreateDirectChatRoom() {
        // Given
        String roomName = "Direct Chat Room";

        // When
        ChatRoomEntity result = ChatRoomEntityFactory.createDirectChatRoom(TEST_CREATOR_ID, roomName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(roomName);
        assertThat(result.getType()).isEqualTo(ChatRoomType.DIRECT);
        assertThat(result.getCreatorId()).isEqualTo(TEST_CREATOR_ID);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getMaxParticipants()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should default unset fields correctly")
    void shouldDefaultUnsetFields() {
        // Given
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .name("Minimal Room")
                .type(ChatRoomType.GROUP)
                .build();

        // When
        ChatRoomEntity result = ChatRoomEntityFactory.createFromRequest(request, TEST_CREATOR_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Minimal Room");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getType()).isEqualTo(ChatRoomType.GROUP);
        assertThat(result.getCreatorId()).isEqualTo(TEST_CREATOR_ID);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getMaxParticipants()).isNull();
    }

    @Test
    @DisplayName("Should handle DIRECT type mapping correctly")
    void shouldHandleDirectTypeMapping() {
        // Given
        ChatRoomCreateRequest request = ChatRequestFixtures.createDirectChatCreateRequest();

        // When
        ChatRoomEntity result = ChatRoomEntityFactory.createFromRequest(request, TEST_CREATOR_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ChatRoomType.DIRECT);
    }

    @Test
    @DisplayName("Should handle GROUP type mapping correctly")
    void shouldHandleGroupTypeMapping() {
        // Given
        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();

        // When
        ChatRoomEntity result = ChatRoomEntityFactory.createFromRequest(request, TEST_CREATOR_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ChatRoomType.GROUP);
    }
}