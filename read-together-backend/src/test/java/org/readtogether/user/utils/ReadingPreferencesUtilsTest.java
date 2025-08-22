package org.readtogether.user.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.VideoQuality;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.entity.ReadingPreferencesEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadingPreferencesUtils Tests")
class ReadingPreferencesUtilsTest {

    @Test
    @DisplayName("Should copy all fields from updates to existing")
    void shouldCopyAllFieldsFromUpdates() {
        ReadingPreferencesEntity existing = new ReadingPreferencesEntity();
        existing.setDefaultLanguage(ReadingPreferencesEntity.Language.ENGLISH);
        existing.setReadingSpeed(ReadingSpeed.NORMAL);
        existing.setSubtitlesEnabled(true);
        existing.setAutoplay(false);
        existing.setVideoQuality(VideoQuality.HIGH);
        existing.setFontSize(FontSize.MEDIUM);
        existing.setTheme(Theme.LIGHT);

        ReadingPreferencesEntity updates = new ReadingPreferencesEntity();
        updates.setDefaultLanguage(ReadingPreferencesEntity.Language.TURKISH);
        updates.setReadingSpeed(ReadingSpeed.FASTER);
        updates.setSubtitlesEnabled(false);
        updates.setAutoplay(true);
        updates.setVideoQuality(VideoQuality.MEDIUM);
        updates.setFontSize(FontSize.LARGE);
        updates.setTheme(Theme.DARK);

        ReadingPreferencesUtils.applyUpdates(existing, updates);

        assertThat(existing.getDefaultLanguage()).isEqualTo(ReadingPreferencesEntity.Language.TURKISH);
        assertThat(existing.getReadingSpeed()).isEqualTo(ReadingSpeed.FASTER);
        assertThat(existing.isSubtitlesEnabled()).isFalse();
        assertThat(existing.isAutoplay()).isTrue();
        assertThat(existing.getVideoQuality()).isEqualTo(VideoQuality.MEDIUM);
        assertThat(existing.getFontSize()).isEqualTo(FontSize.LARGE);
        assertThat(existing.getTheme()).isEqualTo(Theme.DARK);
    }

    @Test
    @DisplayName("Should return correct speed multiplier for each speed")
    void shouldReturnCorrectSpeedMultiplierForEachSpeed() {
        assertThat(ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.SLOW)).isEqualTo(0.75);
        assertThat(ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.NORMAL)).isEqualTo(1.0);
        assertThat(ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.FAST)).isEqualTo(1.25);
        assertThat(ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.FASTER)).isEqualTo(1.5);
    }

    @Test
    @DisplayName("Should map video quality enum to resolution string")
    void shouldMapVideoQualityEnumToResolutionString() {
        assertThat(ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.LOW)).isEqualTo("480p");
        assertThat(ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.MEDIUM)).isEqualTo("720p");
        assertThat(ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.HIGH)).isEqualTo("1080p");
        assertThat(ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.AUTO)).isEqualTo("auto");
    }

    @Test
    @DisplayName("Should map language enum to ISO code")
    void shouldMapLanguageEnumToIsoCode() {
        assertThat(ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.ENGLISH)).isEqualTo("en");
        assertThat(ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.TURKISH)).isEqualTo("tr");
        assertThat(ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.SPANISH)).isEqualTo("es");
        assertThat(ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.FRENCH)).isEqualTo("fr");
        assertThat(ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.GERMAN)).isEqualTo("de");
    }
}
