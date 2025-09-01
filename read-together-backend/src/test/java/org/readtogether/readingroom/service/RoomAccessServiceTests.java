package org.readtogether.readingroom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomInvitationEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.readtogether.readingroom.common.enums.InvitationStatus.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoomAccessService Tests")
class RoomAccessServiceTests {

    @Mock
    private RoomInvitationService roomInvitationService;

    @Mock
    private ReadingRoomService readingRoomService;

    @Mock
    private RoomSettingsService roomSettingsService;

    @InjectMocks
    private RoomAccessService roomAccessService;

    private ReadingRoomEntity room;
    private ReadingRoomEntity privateRoom;
    private UserEntity user;
    private ReadingRoomInvitationEntity invitation;
    private String invitationToken;
    private UUID userId;

    @BeforeEach
    void setUp() {
        room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        privateRoom = ReadingRoomEntityFixtures.createPrivateRoomEntity();
        user = UserEntityFixtures.createDefaultUserEntity();
        invitation = ReadingRoomInvitationEntityFixtures.createDirectInvitation(
                room, UserEntityFixtures.createSecondaryUserEntity(), user, Instant.now().plus(1, ChronoUnit.DAYS));
        invitationToken = invitation.getInvitationToken();
        userId = user.getId();
    }

    @Test
    @DisplayName("Should get room from invitation token")
    void shouldGetRoomFromInvitationToken() {
        // Given
        InvitationResponse invitationResponse = InvitationResponse.builder()
                .invitationToken(invitationToken)
                .readingRoomId(room.getId())
                .roomTitle(room.getTitle())
                .build();

        when(roomInvitationService.getInvitationByToken(invitationToken))
                .thenReturn(invitationResponse);

        // When
        InvitationResponse result = roomAccessService.getRoomFromInvitation(invitationToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getInvitationToken()).isEqualTo(invitationToken);
        assertThat(result.getReadingRoomId()).isEqualTo(room.getId());

        verify(roomInvitationService).getInvitationByToken(invitationToken);
    }

    @Test
    @DisplayName("Should join room via invitation with password validation")
    void shouldJoinRoomViaInvitationWithPasswordValidation() {
        // Given
        String password = "correctPassword";
        JoinRoomRequest joinRequest = ReadingRoomRequestFixtures.createJoinRoomRequest("ROOM001", password);

        InvitationResponse invitationResponse = InvitationResponse.builder()
                .readingRoomId(room.getId())
                .build();

        InvitationResponse acceptedInvitationResponse = InvitationResponse.builder()
                .readingRoomId(room.getId())
                .status(ACCEPTED)
                .build();

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .currentParticipants(2)
                .build();

        when(roomInvitationService.getInvitationByToken(invitationToken)).thenReturn(invitationResponse);
        when(roomSettingsService.validateRoomPassword(room.getId(), password)).thenReturn(true);
        when(roomInvitationService.acceptInvitation(invitationToken, userId))
                .thenReturn(acceptedInvitationResponse);
        when(readingRoomService.joinRoom(room.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomViaInvitation(
                invitationToken, joinRequest, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(room.getId());
        assertThat(result.getCurrentParticipants()).isEqualTo(2);

        verify(roomInvitationService).getInvitationByToken(invitationToken);
        verify(roomSettingsService).validateRoomPassword(room.getId(), password);
        verify(roomInvitationService).acceptInvitation(invitationToken, userId);
        verify(readingRoomService).joinRoom(room.getId(), userId);
    }

    @Test
    @DisplayName("Should join room via invitation without password when room is public")
    void shouldJoinRoomViaInvitationWithoutPasswordWhenRoomIsPublic() {
        // Given
        InvitationResponse invitationResponse = InvitationResponse.builder()
                .readingRoomId(room.getId())
                .build();

        InvitationResponse acceptedInvitationResponse = InvitationResponse.builder()
                .readingRoomId(room.getId())
                .status(ACCEPTED)
                .build();

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .currentParticipants(1)
                .build();

        when(roomInvitationService.getInvitationByToken(invitationToken)).thenReturn(invitationResponse);
        when(roomInvitationService.acceptInvitation(invitationToken, userId))
                .thenReturn(acceptedInvitationResponse);
        when(readingRoomService.joinRoom(room.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomViaInvitation(
                invitationToken, null, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(room.getId());

        verify(roomInvitationService).getInvitationByToken(invitationToken);
        verify(roomSettingsService, never()).validateRoomPassword(any(), anyString());
        verify(roomInvitationService).acceptInvitation(invitationToken, userId);
        verify(readingRoomService).joinRoom(room.getId(), userId);
    }

    @Test
    @DisplayName("Should throw when invalid password via invitation")
    void shouldThrowWhenInvalidPasswordViaInvitation() {
        // Given
        String wrongPassword = "wrongPassword";
        JoinRoomRequest joinRequest = ReadingRoomRequestFixtures.createJoinRoomRequest("ROOM001", wrongPassword);

        InvitationResponse invitationResponse = InvitationResponse.builder()
                .readingRoomId(room.getId())
                .build();

        when(roomInvitationService.getInvitationByToken(invitationToken)).thenReturn(invitationResponse);
        when(roomSettingsService.validateRoomPassword(room.getId(), wrongPassword)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> roomAccessService.joinRoomViaInvitation(
                invitationToken, joinRequest, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid room password");

        verify(roomInvitationService).getInvitationByToken(invitationToken);
        verify(roomSettingsService).validateRoomPassword(room.getId(), wrongPassword);
        verify(roomInvitationService, never()).acceptInvitation(anyString(), any());
        verify(readingRoomService, never()).joinRoom(any(), any());
    }

    @Test
    @DisplayName("Should join room by code with password validation")
    void shouldJoinRoomByCodeWithPasswordValidation() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createPasswordProtectedJoinRequest();
        String roomCode = request.getRoomCode();
        String password = request.getPassword();

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(privateRoom.getId())
                .roomCode(roomCode)
                .title(privateRoom.getTitle())
                .currentParticipants(1)
                .build();

        when(readingRoomService.getRoomByCode(roomCode)).thenReturn(roomResponse);
        when(roomSettingsService.validateRoomPassword(privateRoom.getId(), password)).thenReturn(true);
        when(readingRoomService.joinRoom(privateRoom.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomByCode(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoomCode()).isEqualTo(roomCode);

        verify(readingRoomService).getRoomByCode(roomCode);
        verify(roomSettingsService).validateRoomPassword(privateRoom.getId(), password);
        verify(readingRoomService).joinRoom(privateRoom.getId(), userId);
    }

    @Test
    @DisplayName("Should join room by code without password validation for public rooms")
    void shouldJoinRoomByCodeWithoutPasswordValidationForPublicRooms() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createDefaultJoinRoomRequest();
        String roomCode = request.getRoomCode();

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(room.getId())
                .roomCode(roomCode)
                .title(room.getTitle())
                .isPublic(true)
                .currentParticipants(3)
                .build();

        when(readingRoomService.getRoomByCode(roomCode)).thenReturn(roomResponse);
        when(readingRoomService.joinRoom(room.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomByCode(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoomCode()).isEqualTo(roomCode);
        assertThat(result.getCurrentParticipants()).isEqualTo(3);

        verify(readingRoomService).getRoomByCode(roomCode);
        verify(roomSettingsService, never()).validateRoomPassword(any(), anyString());
        verify(readingRoomService).joinRoom(room.getId(), userId);
    }

    @Test
    @DisplayName("Should throw when invalid password by code")
    void shouldThrowWhenInvalidPasswordByCode() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createJoinRoomRequest("PRIV001", "wrongPassword");

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(privateRoom.getId())
                .roomCode(request.getRoomCode())
                .title(privateRoom.getTitle())
                .isPublic(false)
                .build();

        when(readingRoomService.getRoomByCode(request.getRoomCode())).thenReturn(roomResponse);
        when(roomSettingsService.validateRoomPassword(privateRoom.getId(), "wrongPassword")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> roomAccessService.joinRoomByCode(request, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid room password");

        verify(readingRoomService).getRoomByCode(request.getRoomCode());
        verify(roomSettingsService).validateRoomPassword(privateRoom.getId(), "wrongPassword");
        verify(readingRoomService, never()).joinRoom(any(), any());
    }

    @Test
    @DisplayName("Should handle empty password for private room")
    void shouldHandleEmptyPasswordForPrivateRoom() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createJoinRoomRequest("PRIV001", null);

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(privateRoom.getId())
                .roomCode(request.getRoomCode())
                .title(privateRoom.getTitle())
                .isPublic(false)
                .build();

        when(readingRoomService.getRoomByCode(request.getRoomCode())).thenReturn(roomResponse);
        when(readingRoomService.joinRoom(privateRoom.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomByCode(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(privateRoom.getId());

        verify(readingRoomService).getRoomByCode(request.getRoomCode());
        verify(readingRoomService).joinRoom(privateRoom.getId(), userId);
        verify(roomSettingsService, never()).validateRoomPassword(any(), any()); // No validation when password is null
    }

    @Test
    @DisplayName("Should handle blank password for private room")
    void shouldHandleBlankPasswordForPrivateRoom() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createJoinRoomRequest("PRIV001", "   ");

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(privateRoom.getId())
                .roomCode(request.getRoomCode())
                .title(privateRoom.getTitle())
                .isPublic(false)
                .build();

        when(readingRoomService.getRoomByCode(request.getRoomCode())).thenReturn(roomResponse);
        when(roomSettingsService.validateRoomPassword(privateRoom.getId(), "   ")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> roomAccessService.joinRoomByCode(request, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid room password");

        verify(roomSettingsService).validateRoomPassword(privateRoom.getId(), "   ");
    }

    @Test
    @DisplayName("Should join private room with correct password")
    void shouldJoinPrivateRoomWithCorrectPassword() {
        // Given
        String correctPassword = "correctPassword123";
        JoinRoomRequest request = ReadingRoomRequestFixtures.createJoinRoomRequest("PRIV001", correctPassword);

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(privateRoom.getId())
                .roomCode(request.getRoomCode())
                .title(privateRoom.getTitle())
                .isPublic(false)
                .currentParticipants(2)
                .build();

        when(readingRoomService.getRoomByCode(request.getRoomCode())).thenReturn(roomResponse);
        when(roomSettingsService.validateRoomPassword(privateRoom.getId(), correctPassword)).thenReturn(true);
        when(readingRoomService.joinRoom(privateRoom.getId(), userId)).thenReturn(roomResponse);

        // When
        ReadingRoomResponse result = roomAccessService.joinRoomByCode(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(privateRoom.getId());
        assertThat(result.getCurrentParticipants()).isEqualTo(2);

        verify(roomSettingsService).validateRoomPassword(privateRoom.getId(), correctPassword);
        verify(readingRoomService).joinRoom(privateRoom.getId(), userId);
    }

    @Test
    @DisplayName("Should propagate room service exceptions")
    void shouldPropagateRoomServiceExceptions() {
        // Given
        JoinRoomRequest request = ReadingRoomRequestFixtures.createDefaultJoinRoomRequest();

        ReadingRoomResponse roomResponse = ReadingRoomResponse.builder()
                .id(room.getId())
                .roomCode(request.getRoomCode())
                .title(room.getTitle())
                .isPublic(true)
                .build();

        when(readingRoomService.getRoomByCode(request.getRoomCode())).thenReturn(roomResponse);
        when(readingRoomService.joinRoom(room.getId(), userId))
                .thenThrow(new RuntimeException("Room is full"));

        // When / Then
        assertThatThrownBy(() -> roomAccessService.joinRoomByCode(request, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room is full");

        verify(readingRoomService).getRoomByCode(request.getRoomCode());
        verify(readingRoomService).joinRoom(room.getId(), userId);
    }

    @Test
    @DisplayName("Should propagate invitation service exceptions")
    void shouldPropagateInvitationServiceExceptions() {
        // Given
        when(roomInvitationService.acceptInvitation(invitationToken, userId))
                .thenThrow(new RuntimeException("Invitation has expired"));

        // When / Then
        assertThatThrownBy(() -> roomAccessService.joinRoomViaInvitation(invitationToken, null, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invitation has expired");

        verify(roomInvitationService).acceptInvitation(invitationToken, userId);
        verify(readingRoomService, never()).joinRoom(any(), any());
    }
}