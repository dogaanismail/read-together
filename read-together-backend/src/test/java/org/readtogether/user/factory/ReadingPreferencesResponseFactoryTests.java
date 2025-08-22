package org.readtogether.user.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.model.response.ReadingPreferencesResponse;
import org.readtogether.user.fixtures.ReadingPreferencesFixtures;
import org.readtogether.user.fixtures.ResponseFixtures;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadingPreferencesResponseFactory Tests")
class ReadingPreferencesResponseFactoryTests {

    @Test
    @DisplayName("Should create response from entity with default preferences")
    void shouldCreateResponseFromEntityWithDefaultPreferences() {
        // Given
        ReadingPreferencesEntity entity = ReadingPreferencesFixtures.createDefaultReadingPreferencesEntity();

        // When
        ReadingPreferencesResponse result = ReadingPreferencesResponseFactory.createFromEntity(entity);

        // Then
        ReadingPreferencesResponse expected = ResponseFixtures.createDefaultReadingPreferencesResponse();

        assertThat(result).isNotNull();
        assertThat(result.getDefaultLanguage()).isEqualTo(expected.getDefaultLanguage());
        assertThat(result.getReadingSpeed()).isEqualTo(expected.getReadingSpeed());
        assertThat(result.isSubtitlesEnabled()).isEqualTo(expected.isSubtitlesEnabled());
        assertThat(result.isAutoplay()).isEqualTo(expected.isAutoplay());
        assertThat(result.getQuality()).isEqualTo(expected.getQuality());
        assertThat(result.getFontSize()).isEqualTo(expected.getFontSize());
        assertThat(result.getTheme()).isEqualTo(expected.getTheme());
    }

}
