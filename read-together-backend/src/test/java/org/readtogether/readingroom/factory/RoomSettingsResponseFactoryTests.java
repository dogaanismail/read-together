package org.readtogether.readingroom.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.fixtures.ReadingRoomEntityFixtures;
import org.readtogether.readingroom.fixtures.ReadingRoomSettingsEntityFixtures;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.ENGLISH;
import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.SPANISH;

class RoomSettingsResponseFactoryTests {

    @Test
    @DisplayName("Should map entity to response")
    void shouldMapEntityToResponse() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createDefaultSettingsEntity(
                ReadingRoomEntityFixtures.createDefaultRoomEntity()
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getId()).isEqualTo(settings.getId());
        assertThat(result.getReadingRoomId()).isEqualTo(settings.getReadingRoom().getId());
        assertThat(result.getIsPublic()).isEqualTo(settings.isPublic());
        assertThat(result.getHasPassword()).isFalse(); // no password set
        assertThat(result.getRequireHostApproval()).isEqualTo(settings.isRequireHostApproval());
        assertThat(result.getEnableVideo()).isEqualTo(settings.isEnableVideo());
        assertThat(result.getEnableAudio()).isEqualTo(settings.isEnableAudio());
        assertThat(result.getEnableChat()).isEqualTo(settings.isEnableChat());
        assertThat(result.getAllowRecording()).isEqualTo(settings.isAllowRecording());
        assertThat(result.getAutoMuteNewJoiners()).isEqualTo(settings.isAutoMuteNewJoiners());
        assertThat(result.getRoomVolume()).isEqualTo(settings.getRoomVolume());
        assertThat(result.getEnableLiveTranscription()).isEqualTo(settings.isEnableLiveTranscription());
        assertThat(result.getTranscriptionLanguage()).isEqualTo(settings.getTranscriptionLanguage());
        assertThat(result.getEnableSpeakerIdentification()).isEqualTo(settings.isEnableSpeakerIdentification());
        assertThat(result.getDownloadableTranscripts()).isEqualTo(settings.isDownloadableTranscripts());
        assertThat(result.getPronunciationHelp()).isEqualTo(settings.isPronunciationHelp());
    }

    @Test
    @DisplayName("Should map transcription language display name")
    void shouldMapTranscriptionLanguageDisplayName() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createDefaultSettingsEntity(
                ReadingRoomEntityFixtures.createDefaultRoomEntity()
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getTranscriptionLanguage()).isEqualTo(ENGLISH);
        assertThat(result.getTranscriptionLanguageDisplay()).isEqualTo("English");
    }

    @Test
    @DisplayName("Should handle password protected settings")
    void shouldHandlePasswordProtectedSettings() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createPasswordProtectedSettings(
                ReadingRoomEntityFixtures.createPrivateRoomEntity(),
                "hashedPassword123"
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getHasPassword()).isTrue();
        assertThat(result.getIsPublic()).isFalse();
        // Password value should not be exposed in response
        assertThat(result.toString()).doesNotContain("hashedPassword123");
    }

    @Test
    @DisplayName("Should handle restrictive settings")
    void shouldHandleRestrictiveSettings() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createRestrictiveSettingsEntity(
                ReadingRoomEntityFixtures.createPrivateRoomEntity()
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getIsPublic()).isFalse();
        assertThat(result.getRequireHostApproval()).isTrue();
        assertThat(result.getEnableChat()).isFalse();
        assertThat(result.getEnableAudio()).isFalse();
        assertThat(result.getEnableVideo()).isFalse();
        assertThat(result.getHasPassword()).isTrue();
        assertThat(result.getTranscriptionLanguage()).isEqualTo(SPANISH);
        assertThat(result.getTranscriptionLanguageDisplay()).isEqualTo("Spanish");
    }

    @Test
    @DisplayName("Should handle settings with different transcription languages")
    void shouldHandleSettingsWithDifferentTranscriptionLanguages() {
        // Given
        ReadingRoomSettingsEntity spanishSettings = ReadingRoomSettingsEntityFixtures.createRestrictiveSettingsEntity(
                ReadingRoomEntityFixtures.createPrivateRoomEntity()
        );
        ReadingRoomSettingsEntity englishSettings = ReadingRoomSettingsEntityFixtures.createDefaultSettingsEntity(
                ReadingRoomEntityFixtures.createDefaultRoomEntity()
        );

        // When
        RoomSettingsResponse spanishResponse = RoomSettingsResponseFactory.createResponse(spanishSettings);
        RoomSettingsResponse englishResponse = RoomSettingsResponseFactory.createResponse(englishSettings);

        // Then
        assertThat(spanishResponse.getTranscriptionLanguage()).isEqualTo(SPANISH);
        assertThat(spanishResponse.getTranscriptionLanguageDisplay()).isEqualTo("Spanish");
        assertThat(englishResponse.getTranscriptionLanguage()).isEqualTo(ENGLISH);
        assertThat(englishResponse.getTranscriptionLanguageDisplay()).isEqualTo("English");
    }

    @Test
    @DisplayName("Should handle empty password correctly")
    void shouldHandleEmptyPasswordCorrectly() {
        // Given
        ReadingRoomSettingsEntity settingsWithEmptyPassword = ReadingRoomSettingsEntityFixtures.createOpenSettings(
                ReadingRoomEntityFixtures.createDefaultRoomEntity()
        );
        // Open settings have null password

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settingsWithEmptyPassword);

        // Then
        assertThat(result.getHasPassword()).isFalse();
        assertThat(result.getIsPublic()).isTrue();
    }

    @Test
    @DisplayName("Should handle blank password correctly")
    void shouldHandleBlankPasswordCorrectly() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createSettingsEntity(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                true,
                false,
                true,
                true,
                true,
                true,
                "", // empty string password
                80,
                false,
                ENGLISH,
                true,
                true,
                true
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getHasPassword()).isFalse(); // empty string should be considered as no password
    }

    @Test
    @DisplayName("Should map all boolean fields correctly")
    void shouldMapAllBooleanFieldsCorrectly() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createSettingsEntity(
                ReadingRoomEntityFixtures.createDefaultRoomEntity(),
                false, // isPublic
                true,  // requireHostApproval
                false, // enableChat
                true,  // enableAudio
                false, // enableVideo
                true,  // autoMuteNewJoiners
                "password123",
                90,
                true,  // enableLiveTranscription
                ENGLISH,
                false, // enableSpeakerIdentification
                true,  // downloadableTranscripts
                false  // pronunciationHelp
        );

        // When
        RoomSettingsResponse result = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(result.getIsPublic()).isFalse();
        assertThat(result.getRequireHostApproval()).isTrue();
        assertThat(result.getEnableChat()).isFalse();
        assertThat(result.getEnableAudio()).isTrue();
        assertThat(result.getEnableVideo()).isFalse();
        assertThat(result.getAutoMuteNewJoiners()).isTrue();
        assertThat(result.getHasPassword()).isTrue();
        assertThat(result.getRoomVolume()).isEqualTo(90);
        assertThat(result.getEnableLiveTranscription()).isTrue();
        assertThat(result.getEnableSpeakerIdentification()).isFalse();
        assertThat(result.getDownloadableTranscripts()).isTrue();
        assertThat(result.getPronunciationHelp()).isFalse();
    }

    @Test
    @DisplayName("Should create consistent responses for same entity")
    void shouldCreateConsistentResponsesForSameEntity() {
        // Given
        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFixtures.createPrivateSettingsEntity(
                ReadingRoomEntityFixtures.createPrivateRoomEntity()
        );

        // When
        RoomSettingsResponse response1 = RoomSettingsResponseFactory.createResponse(settings);
        RoomSettingsResponse response2 = RoomSettingsResponseFactory.createResponse(settings);

        // Then
        assertThat(response1.getId()).isEqualTo(response2.getId());
        assertThat(response1.getReadingRoomId()).isEqualTo(response2.getReadingRoomId());
        assertThat(response1.getIsPublic()).isEqualTo(response2.getIsPublic());
        assertThat(response1.getHasPassword()).isEqualTo(response2.getHasPassword());
        assertThat(response1.getTranscriptionLanguage()).isEqualTo(response2.getTranscriptionLanguage());
        assertThat(response1.getTranscriptionLanguageDisplay()).isEqualTo(response2.getTranscriptionLanguageDisplay());
    }
}