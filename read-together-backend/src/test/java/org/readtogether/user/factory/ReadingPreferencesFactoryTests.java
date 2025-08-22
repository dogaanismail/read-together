package org.readtogether.user.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.user.fixtures.UserEntityFixtures.DEFAULT_USER_ID;
import static org.readtogether.user.fixtures.UserEntityFixtures.SECONDARY_USER_ID;

@DisplayName("ReadingPreferencesFactory Tests")
class ReadingPreferencesFactoryTests {

    @Test
    @DisplayName("Should create default reading preferences with correct default values")
    void shouldCreateDefaultPreferences() {
        // When
        ReadingPreferencesEntity result = ReadingPreferencesFactory.createDefaultPreferences(DEFAULT_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(result.getDefaultLanguage()).isEqualTo(ReadingPreferencesEntity.Language.ENGLISH);
        assertThat(result.getReadingSpeed()).isEqualTo(ReadingSpeed.NORMAL);
        assertThat(result.isSubtitlesEnabled()).isTrue();
        assertThat(result.isAutoplay()).isFalse();
        assertThat(result.getVideoQuality()).isEqualTo(VideoQuality.HIGH);
        assertThat(result.getFontSize()).isEqualTo(FontSize.MEDIUM);
        assertThat(result.getTheme()).isEqualTo(Theme.LIGHT);
    }


    @Test
    @DisplayName("Should handle different user IDs correctly")
    void shouldHandleDifferentUserIds() {
        // Given
        UUID differentUserId = SECONDARY_USER_ID;

        // When
        ReadingPreferencesEntity result = ReadingPreferencesFactory.createDefaultPreferences(differentUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(differentUserId);
        assertThat(result.getUserId()).isNotEqualTo(DEFAULT_USER_ID);
    }
}
