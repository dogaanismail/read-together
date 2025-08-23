package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.ENGLISH;

class ReadingRoomSettingsEntityFactoryTests {

    @Test
    @DisplayName("Should create default settings")
    void shouldCreateDefaultSettings() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createDefaultRoomEntity();

        // When
        ReadingRoomSettingsEntity result = ReadingRoomSettingsEntityFactory.createDefaultSettings(room);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(room);
        assertThat(result.isPublic()).isEqualTo(room.isPublic());
        assertThat(result.isRequireHostApproval()).isFalse();
        assertThat(result.isEnableVideo()).isTrue();
        assertThat(result.isEnableAudio()).isTrue();
        assertThat(result.isEnableChat()).isTrue();
        assertThat(result.isAllowRecording()).isFalse();
        assertThat(result.isAutoMuteNewJoiners()).isTrue();
        assertThat(result.getRoomVolume()).isEqualTo(80);
        assertThat(result.isEnableLiveTranscription()).isFalse();
        assertThat(result.getTranscriptionLanguage()).isEqualTo(ENGLISH);
        assertThat(result.isEnableSpeakerIdentification()).isTrue();
        assertThat(result.isDownloadableTranscripts()).isTrue();
        assertThat(result.isPronunciationHelp()).isTrue();
        assertThat(result.getPassword()).isNull(); // no password by default
    }

    @Test
    @DisplayName("Should inherit public setting from room")
    void shouldInheritPublicSettingFromRoom() {
        // Given
        ReadingRoomEntity publicRoom = ReadingRoomEntityFixtures.createDefaultRoomEntity(); // public
        ReadingRoomEntity privateRoom = ReadingRoomEntityFixtures.createPrivateRoomEntity(); // private

        // When
        ReadingRoomSettingsEntity publicSettings = ReadingRoomSettingsEntityFactory.createDefaultSettings(publicRoom);
        ReadingRoomSettingsEntity privateSettings = ReadingRoomSettingsEntityFactory.createDefaultSettings(privateRoom);

        // Then
        assertThat(publicSettings.isPublic()).isTrue();
        assertThat(privateSettings.isPublic()).isFalse();

        assertThat(publicSettings.isEnableVideo()).isEqualTo(privateSettings.isEnableVideo());
        assertThat(publicSettings.isEnableAudio()).isEqualTo(privateSettings.isEnableAudio());
        assertThat(publicSettings.getRoomVolume()).isEqualTo(privateSettings.getRoomVolume());
    }

    @Test
    @DisplayName("Should create settings with consistent defaults")
    void shouldCreateSettingsWithConsistentDefaults() {
        // Given
        ReadingRoomEntity room1 = ReadingRoomEntityFixtures.createDefaultRoomEntity();
        ReadingRoomEntity room2 = ReadingRoomEntityFixtures.createActiveRoomEntity();

        // When
        ReadingRoomSettingsEntity settings1 = ReadingRoomSettingsEntityFactory.createDefaultSettings(room1);
        ReadingRoomSettingsEntity settings2 = ReadingRoomSettingsEntityFactory.createDefaultSettings(room2);

        // Then - all non-room-specific defaults should be identical
        assertThat(settings1.isRequireHostApproval()).isEqualTo(settings2.isRequireHostApproval());
        assertThat(settings1.isEnableVideo()).isEqualTo(settings2.isEnableVideo());
        assertThat(settings1.isEnableAudio()).isEqualTo(settings2.isEnableAudio());
        assertThat(settings1.isEnableChat()).isEqualTo(settings2.isEnableChat());
        assertThat(settings1.isAllowRecording()).isEqualTo(settings2.isAllowRecording());
        assertThat(settings1.isAutoMuteNewJoiners()).isEqualTo(settings2.isAutoMuteNewJoiners());
        assertThat(settings1.getRoomVolume()).isEqualTo(settings2.getRoomVolume());
        assertThat(settings1.isEnableLiveTranscription()).isEqualTo(settings2.isEnableLiveTranscription());
        assertThat(settings1.getTranscriptionLanguage()).isEqualTo(settings2.getTranscriptionLanguage());
        assertThat(settings1.isEnableSpeakerIdentification()).isEqualTo(settings2.isEnableSpeakerIdentification());
        assertThat(settings1.isDownloadableTranscripts()).isEqualTo(settings2.isDownloadableTranscripts());
        assertThat(settings1.isPronunciationHelp()).isEqualTo(settings2.isPronunciationHelp());
    }

    @Test
    @DisplayName("Should create settings for completed room")
    void shouldCreateSettingsForCompletedRoom() {
        // Given
        ReadingRoomEntity completedRoom = ReadingRoomEntityFixtures.createCompletedRoomEntity();

        // When
        ReadingRoomSettingsEntity result = ReadingRoomSettingsEntityFactory.createDefaultSettings(completedRoom);

        // Then
        assertThat(result.getReadingRoom()).isEqualTo(completedRoom);
        assertThat(result.isPublic()).isEqualTo(completedRoom.isPublic());
        // All other defaults should still apply regardless of room status
        assertThat(result.isEnableVideo()).isTrue();
        assertThat(result.isEnableAudio()).isTrue();
        assertThat(result.getRoomVolume()).isEqualTo(80);
    }

    @Test
    @DisplayName("Should create settings with room reference")
    void shouldCreateSettingsWithRoomReference() {
        // Given
        ReadingRoomEntity room = ReadingRoomEntityFixtures.createFullRoomEntity();

        // When
        ReadingRoomSettingsEntity result = ReadingRoomSettingsEntityFactory.createDefaultSettings(room);

        // Then
        assertThat(result.getReadingRoom()).isNotNull();
        assertThat(result.getReadingRoom()).isSameAs(room);
        assertThat(result.getReadingRoom().getId()).isEqualTo(room.getId());
        assertThat(result.getReadingRoom().getTitle()).isEqualTo(room.getTitle());
    }
}