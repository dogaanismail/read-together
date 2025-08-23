package org.readtogether.readingroom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomSettingsEntityFixtures;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.readingroom.repository.ReadingRoomSettingsRepository;
import org.readtogether.user.fixtures.UserEntityFixtures;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomSettingsService Tests")
class RoomSettingsServiceTests {

    @Mock
    private ReadingRoomSettingsRepository settingsRepository;

    @Mock
    private ReadingRoomRepository roomRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RoomSettingsService roomSettingsService;

    private ReadingRoomEntity room;
    private ReadingRoomSettingsEntity settings;
    private UUID hostId;
    private UUID nonHostId;

    @BeforeEach
    void setUp() {
        room = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        settings = ReadingRoomSettingsEntityFixtures.createDefaultSettingsEntity(room);
        hostId = room.getHost().getId();
        nonHostId = UserEntityFixtures.createSecondaryUserEntity().getId();
    }

    @Test
    @DisplayName("Should update room settings as host")
    void shouldUpdateRoomSettingsAsHost() {
        // Given
        UUID roomId = room.getId();
        UpdateRoomSettingsRequest request = ReadingRoomRequestFixtures.createPrivateUpdateRoomSettingsRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(settingsRepository.save(any(ReadingRoomSettingsEntity.class))).thenReturn(settings);

        // When
        RoomSettingsResponse result = roomSettingsService.updateRoomSettings(roomId, request, hostId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReadingRoomId()).isEqualTo(roomId);

        verify(roomRepository).findById(roomId);
        verify(settingsRepository).findByReadingRoomId(roomId);
        verify(settingsRepository).save(any(ReadingRoomSettingsEntity.class));
        verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    @DisplayName("Should throw when non-host updates settings")
    void shouldThrowWhenNonHostUpdatesSettings() {
        // Given
        UUID roomId = room.getId();
        UpdateRoomSettingsRequest request = ReadingRoomRequestFixtures.createDefaultUpdateRoomSettingsRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // When / Then
        assertThatThrownBy(() -> roomSettingsService.updateRoomSettings(roomId, request, nonHostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only the host can update room settings");

        verify(settingsRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should get room settings")
    void shouldGetRoomSettings() {
        // Given
        UUID roomId = room.getId();

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));

        // When
        RoomSettingsResponse result = roomSettingsService.getRoomSettings(roomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReadingRoomId()).isEqualTo(roomId);
        assertThat(result.getIsPublic()).isEqualTo(settings.isPublic());

        verify(settingsRepository).findByReadingRoomId(roomId);
    }

    @Test
    @DisplayName("Should validate password matches")
    void shouldValidatePasswordMatches() {
        // Given
        UUID roomId = room.getId();
        String plainPassword = "testPassword123";
        String hashedPassword = "hashedPassword123";
        
        ReadingRoomSettingsEntity passwordSettings = ReadingRoomSettingsEntityFixtures
                .createPasswordProtectedSettings(room, hashedPassword);

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(passwordSettings));
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        // When
        boolean result = roomSettingsService.validateRoomPassword(roomId, plainPassword);

        // Then
        assertThat(result).isTrue();

        verify(settingsRepository).findByReadingRoomId(roomId);
        verify(passwordEncoder).matches(plainPassword, hashedPassword);
    }

    @Test
    @DisplayName("Should return false for empty or missing password")
    void shouldReturnFalseForEmptyOrMissingPassword() {
        // Given
        UUID roomId = room.getId();

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));
        // settings have no password (null)

        // When
        boolean resultNull = roomSettingsService.validateRoomPassword(roomId, "anyPassword");
        boolean resultEmpty = roomSettingsService.validateRoomPassword(roomId, "");
        boolean resultBlank = roomSettingsService.validateRoomPassword(roomId, "   ");

        // Then
        assertThat(resultNull).isFalse();
        assertThat(resultEmpty).isFalse();
        assertThat(resultBlank).isFalse();

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return false when password doesn't match")
    void shouldReturnFalseWhenPasswordDoesntMatch() {
        // Given
        UUID roomId = room.getId();
        String plainPassword = "wrongPassword";
        String hashedPassword = "hashedPassword123";
        
        ReadingRoomSettingsEntity passwordSettings = ReadingRoomSettingsEntityFixtures
                .createPasswordProtectedSettings(room, hashedPassword);

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(passwordSettings));
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(false);

        // When
        boolean result = roomSettingsService.validateRoomPassword(roomId, plainPassword);

        // Then
        assertThat(result).isFalse();

        verify(passwordEncoder).matches(plainPassword, hashedPassword);
    }

    @Test
    @DisplayName("Should create default settings on create")
    void shouldCreateDefaultSettingsOnCreate() {
        // Given
        ReadingRoomEntity newRoom = ReadingRoomEntityFixtures.createActiveRoomEntity();

        when(settingsRepository.save(any(ReadingRoomSettingsEntity.class)))
                .thenReturn(ReadingRoomSettingsEntityFixtures.createDefaultSettingsEntity(newRoom));

        // When
        roomSettingsService.createDefaultSettings(newRoom);

        // Then
        verify(settingsRepository).save(any(ReadingRoomSettingsEntity.class));
    }

    @Test
    @DisplayName("Should create settings when none exist for room")
    void shouldCreateSettingsWhenNoneExistForRoom() {
        // Given
        UUID roomId = room.getId();
        UpdateRoomSettingsRequest request = ReadingRoomRequestFixtures.createDefaultUpdateRoomSettingsRequest();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.empty());
        when(settingsRepository.save(any(ReadingRoomSettingsEntity.class))).thenReturn(settings);

        // When
        RoomSettingsResponse result = roomSettingsService.updateRoomSettings(roomId, request, hostId);

        // Then
        assertThat(result).isNotNull();

        verify(settingsRepository).findByReadingRoomId(roomId);
        verify(settingsRepository).save(any(ReadingRoomSettingsEntity.class));
    }

    @Test
    @DisplayName("Should handle password encoding correctly")
    void shouldHandlePasswordEncodingCorrectly() {
        // Given
        UUID roomId = room.getId();
        UpdateRoomSettingsRequest requestWithPassword = ReadingRoomRequestFixtures
                .createUpdateRoomSettingsRequest(false, true, true, true, true, true, "newPassword", 75);
        String encodedPassword = "encodedNewPassword";

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));
        when(passwordEncoder.encode("newPassword")).thenReturn(encodedPassword);
        when(settingsRepository.save(any(ReadingRoomSettingsEntity.class))).thenReturn(settings);

        // When
        roomSettingsService.updateRoomSettings(roomId, requestWithPassword, hostId);

        // Then
        verify(passwordEncoder).encode("newPassword");
        verify(settingsRepository).save(any(ReadingRoomSettingsEntity.class));
    }

    @Test
    @DisplayName("Should not encode password when not provided")
    void shouldNotEncodePasswordWhenNotProvided() {
        // Given
        UUID roomId = room.getId();
        UpdateRoomSettingsRequest requestWithoutPassword = ReadingRoomRequestFixtures
                .createUpdateRoomSettingsRequest(true, false, true, true, true, false, null, 80);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(ReadingRoomSettingsEntity.class))).thenReturn(settings);

        // When
        roomSettingsService.updateRoomSettings(roomId, requestWithoutPassword, hostId);

        // Then
        verify(passwordEncoder, never()).encode(anyString());
        verify(settingsRepository).save(any(ReadingRoomSettingsEntity.class));
    }

    @Test
    @DisplayName("Should get settings entity")
    void shouldGetSettingsEntity() {
        // Given
        UUID roomId = room.getId();

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.of(settings));

        // When
        ReadingRoomSettingsEntity result = roomSettingsService.getSettingsEntity(roomId);

        // Then
        assertThat(result).isEqualTo(settings);
        assertThat(result.getReadingRoom()).isEqualTo(room);

        verify(settingsRepository).findByReadingRoomId(roomId);
    }

    @Test
    @DisplayName("Should throw when room not found for settings update")
    void shouldThrowWhenRoomNotFoundForSettingsUpdate() {
        // Given
        UUID nonExistentRoomId = UUID.randomUUID();
        UpdateRoomSettingsRequest request = ReadingRoomRequestFixtures.createDefaultUpdateRoomSettingsRequest();

        when(roomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> roomSettingsService.updateRoomSettings(nonExistentRoomId, request, hostId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");

        verify(settingsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return false when settings not found for validation")
    void shouldReturnFalseWhenSettingsNotFoundForValidation() {
        // Given
        UUID roomId = UUID.randomUUID();

        when(settingsRepository.findByReadingRoomId(roomId)).thenReturn(Optional.empty());

        // When
        boolean result = roomSettingsService.validateRoomPassword(roomId, "password");

        // Then
        assertThat(result).isFalse();

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}