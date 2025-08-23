package org.readtogether.readingroom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomParticipantEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.repository.ReadingRoomParticipantRepository;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.UserEntityFixtures;
import org.readtogether.user.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadingRoomService Tests")
class ReadingRoomServiceTests {

    @Mock
    private ReadingRoomRepository readingRoomRepository;

    @Mock
    private ReadingRoomParticipantRepository participantRepository;

    @Mock
    private UserService userService;

    @Mock
    private RoomSettingsService roomSettingsService;

    @InjectMocks
    private ReadingRoomService readingRoomService;

    private UserEntity hostUser;
    private UserEntity participantUser;
    private ReadingRoomEntity room;

    @BeforeEach
    void setUp() {
        hostUser = UserEntityFixtures.createDefaultUserEntity();
        participantUser = UserEntityFixtures.createSecondaryUserEntity();
        room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
    }

    @Test
    @DisplayName("Should create room and join host")
    void shouldCreateRoomAndJoinHost() {
        // Given
        CreateReadingRoomRequest request = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        UUID hostId = hostUser.getId();

        when(userService.findUserEntityById(hostId)).thenReturn(hostUser);
        when(readingRoomRepository.save(any(ReadingRoomEntity.class))).thenReturn(room);
        when(participantRepository.save(any(ReadingRoomParticipantEntity.class)))
                .thenReturn(ReadingRoomParticipantEntityFixtures.createJoinedParticipant(room, hostUser));
        when(participantRepository.countActiveParticipantsByRoomId(eq(room.getId())))
                .thenReturn(1);

        // When
        ReadingRoomResponse result = readingRoomService.createRoom(request, hostId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getCurrentParticipants()).isEqualTo(1);
        
        verify(userService).findUserEntityById(hostId);
        verify(readingRoomRepository).save(any(ReadingRoomEntity.class));
        verify(roomSettingsService).createDefaultSettings(any(ReadingRoomEntity.class));
        verify(participantRepository).save(any(ReadingRoomParticipantEntity.class));
    }

    @Test
    @DisplayName("Should join room")
    void shouldJoinRoom() {
        // Given
        UUID roomId = room.getId();
        UUID userId = participantUser.getId();

        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(userId)).thenReturn(participantUser);
        when(participantRepository.findByReadingRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.empty());
        when(participantRepository.countActiveParticipantsByRoomId(roomId))
                .thenReturn(2);
        when(participantRepository.save(any(ReadingRoomParticipantEntity.class)))
                .thenReturn(ReadingRoomParticipantEntityFixtures.createJoinedParticipant(room, participantUser));

        // When
        ReadingRoomResponse result = readingRoomService.joinRoom(roomId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roomId);
        assertThat(result.getCurrentParticipants()).isEqualTo(2);

        verify(readingRoomRepository).findById(roomId);
        verify(userService).findUserEntityById(userId);
        verify(participantRepository).findByReadingRoomIdAndUserId(roomId, userId);
        verify(participantRepository).save(any(ReadingRoomParticipantEntity.class));
    }

    @Test
    @DisplayName("Should throw when joining already joined room")
    void shouldThrowWhenJoiningAlreadyJoined() {
        // Given
        UUID roomId = room.getId();
        UUID userId = participantUser.getId();
        ReadingRoomParticipantEntity existingParticipant = 
                ReadingRoomParticipantEntityFixtures.createJoinedParticipant(room, participantUser);

        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userService.findUserEntityById(userId)).thenReturn(participantUser);
        when(participantRepository.findByReadingRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.of(existingParticipant));

        // When / Then
        assertThatThrownBy(() -> readingRoomService.joinRoom(roomId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User is already in the room");

        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when room is full")
    void shouldThrowWhenRoomIsFull() {
        // Given
        ReadingRoomEntity fullRoom = ReadingRoomEntityFixtures.createFullRoomEntity(); // max 2 participants
        UUID roomId = fullRoom.getId();
        UUID userId = participantUser.getId();

        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(fullRoom));
        when(userService.findUserEntityById(userId)).thenReturn(participantUser);
        when(participantRepository.findByReadingRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.empty());
        when(participantRepository.countActiveParticipantsByRoomId(roomId))
                .thenReturn(2); // already at capacity

        // When / Then
        assertThatThrownBy(() -> readingRoomService.joinRoom(roomId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room is full");

        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should leave room")
    void shouldLeaveRoom() {
        // Given
        UUID roomId = room.getId();
        UUID userId = participantUser.getId();
        ReadingRoomParticipantEntity participant = 
                ReadingRoomParticipantEntityFixtures.createJoinedParticipant(room, participantUser);

        when(participantRepository.findByReadingRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.save(any(ReadingRoomParticipantEntity.class)))
                .thenReturn(participant);

        // When
        readingRoomService.leaveRoom(roomId, userId);

        // Then
        verify(participantRepository).findByReadingRoomIdAndUserId(roomId, userId);
        verify(participantRepository).save(any(ReadingRoomParticipantEntity.class));
    }

    @Test
    @DisplayName("Should throw when leaving room not joined")
    void shouldThrowWhenLeavingRoomNotJoined() {
        // Given
        UUID roomId = room.getId();
        UUID userId = participantUser.getId();

        when(participantRepository.findByReadingRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> readingRoomService.leaveRoom(roomId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User is not in this room");
    }

    @Test
    @DisplayName("Should start room by host")
    void shouldStartRoomByHost() {
        // Given
        UUID roomId = room.getId();
        UUID hostId = hostUser.getId();
        
        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(readingRoomRepository.save(any(ReadingRoomEntity.class))).thenReturn(room);
        when(participantRepository.countActiveParticipantsByRoomId(roomId))
                .thenReturn(3);

        // When
        ReadingRoomResponse result = readingRoomService.startRoom(roomId, hostId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roomId);
        assertThat(result.getCurrentParticipants()).isEqualTo(3);

        verify(readingRoomRepository).findById(roomId);
        verify(readingRoomRepository).save(any(ReadingRoomEntity.class));
    }

    @Test
    @DisplayName("Should throw when start room by non-host")
    void shouldThrowWhenStartRoomByNonHost() {
        // Given
        UUID roomId = room.getId();
        UUID nonHostId = participantUser.getId(); // not the host

        when(readingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // When / Then
        assertThatThrownBy(() -> readingRoomService.startRoom(roomId, nonHostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only the host can start");

        verify(readingRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get public waiting rooms")
    void shouldGetPublicWaitingRooms() {
        // Given
        List<ReadingRoomEntity> publicRooms = Arrays.asList(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                ReadingRoomEntityFixtures.createActiveRoomEntity()
        );

        when(readingRoomRepository.findPublicWaitingRooms())
                .thenReturn(publicRooms);
        when(participantRepository.countActiveParticipantsByRoomId(any(UUID.class)))
                .thenReturn(2);

        // When
        List<ReadingRoomResponse> result = readingRoomService.getPublicWaitingRooms();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsPublic()).isTrue();
        assertThat(result.get(1).getIsPublic()).isTrue();

        verify(readingRoomRepository).findPublicWaitingRooms();
    }

    @Test
    @DisplayName("Should get user hosted rooms")
    void shouldGetUserHostedRooms() {
        // Given
        UUID userId = hostUser.getId();
        List<ReadingRoomEntity> hostedRooms = Arrays.asList(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                ReadingRoomEntityFixtures.createPrivateRoomEntity()
        );

        when(readingRoomRepository.findByHostId(userId))
                .thenReturn(hostedRooms);
        when(participantRepository.countActiveParticipantsByRoomId(any(UUID.class)))
                .thenReturn(1);

        // When
        List<ReadingRoomResponse> result = readingRoomService.getUserHostedRooms(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(result.get(1).getCurrentParticipants()).isEqualTo(1);

        verify(readingRoomRepository).findByHostId(userId);
    }

    @Test
    @DisplayName("Should get room by code")
    void shouldGetRoomByCode() {
        // Given
        String roomCode = "ROOM001";

        when(readingRoomRepository.findByRoomCode(roomCode)).thenReturn(Optional.of(room));
        when(participantRepository.countActiveParticipantsByRoomId(room.getId()))
                .thenReturn(5);

        // When
        ReadingRoomResponse result = readingRoomService.getRoomByCode(roomCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoomCode()).isEqualTo(roomCode);
        assertThat(result.getCurrentParticipants()).isEqualTo(5);

        verify(readingRoomRepository).findByRoomCode(roomCode);
    }

    @Test
    @DisplayName("Should throw when room not found")
    void shouldThrowWhenRoomNotFound() {
        // Given
        UUID nonExistentRoomId = UUID.randomUUID();

        when(readingRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> readingRoomService.joinRoom(nonExistentRoomId, participantUser.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    @DisplayName("Should throw when room code not found")
    void shouldThrowWhenRoomCodeNotFound() {
        // Given
        String invalidRoomCode = "INVALID";

        when(readingRoomRepository.findByRoomCode(invalidRoomCode)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> readingRoomService.getRoomByCode(invalidRoomCode))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");
    }
}