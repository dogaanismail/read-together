package org.readtogether.chat.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.chat.entity.ChatParticipantEntity;
import org.readtogether.chat.common.enums.ParticipantRole;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatParticipantEntityFactory Tests")
class ChatParticipantEntityFactoryTest {

    private static final UUID TEST_ROOM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    @DisplayName("Should create admin participant")
    void shouldCreateAdminParticipant() {
        // When
        ChatParticipantEntity result = ChatParticipantEntityFactory.createAdmin(TEST_ROOM_ID, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getRole()).isEqualTo(ParticipantRole.ADMIN);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUnreadCount()).isZero();
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.getLastReadAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create member participant")
    void shouldCreateMemberParticipant() {
        // When
        ChatParticipantEntity result = ChatParticipantEntityFactory.createMember(TEST_ROOM_ID, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getRole()).isEqualTo(ParticipantRole.MEMBER);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUnreadCount()).isZero();
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.getLastReadAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create participant with custom role")
    void shouldCreateParticipantWithCustomRole() {
        // When
        ChatParticipantEntity result = ChatParticipantEntityFactory.createParticipant(
                TEST_ROOM_ID, 
                TEST_USER_ID, 
                ParticipantRole.ADMIN
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getRole()).isEqualTo(ParticipantRole.ADMIN);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUnreadCount()).isZero();
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.getLastReadAt()).isNotNull();
    }

    @Test
    @DisplayName("Should default unset fields correctly")
    void shouldDefaultUnsetFields() {
        // When
        ChatParticipantEntity result = ChatParticipantEntityFactory.createMember(TEST_ROOM_ID, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUnreadCount()).isZero();
        assertThat(result.getJoinedAt()).isNotNull();
        assertThat(result.getLastReadAt()).isNotNull();
        // Verify timestamps are recent (within the last 10 seconds)
        assertThat(result.getJoinedAt().toEpochMilli())
                .isCloseTo(System.currentTimeMillis(), org.assertj.core.data.Offset.offset(10000L));
        assertThat(result.getLastReadAt().toEpochMilli())
                .isCloseTo(System.currentTimeMillis(), org.assertj.core.data.Offset.offset(10000L));
    }

    @Test
    @DisplayName("Should set correct defaults for different roles")
    void shouldSetCorrectDefaultsForDifferentRoles() {
        // When
        ChatParticipantEntity admin = ChatParticipantEntityFactory.createAdmin(TEST_ROOM_ID, TEST_USER_ID);
        ChatParticipantEntity member = ChatParticipantEntityFactory.createMember(TEST_ROOM_ID, TEST_USER_ID);

        // Then
        assertThat(admin.getRole()).isEqualTo(ParticipantRole.ADMIN);
        assertThat(member.getRole()).isEqualTo(ParticipantRole.MEMBER);
        
        // Both should have the same defaults for other fields
        assertThat(admin.isActive()).isEqualTo(member.isActive()).isTrue();
        assertThat(admin.getUnreadCount()).isEqualTo(member.getUnreadCount()).isZero();
    }
}