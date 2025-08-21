package org.readtogether.user.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.ReadingPreferencesEntity;

@UtilityClass
public class ReadingPreferencesUtils {

    public static void applyUpdates(
            ReadingPreferencesEntity existing,
            ReadingPreferencesEntity updates) {

        existing.setDefaultLanguage(updates.getDefaultLanguage());
        existing.setReadingSpeed(updates.getReadingSpeed());
        existing.setSubtitlesEnabled(updates.isSubtitlesEnabled());
        existing.setAutoplay(updates.isAutoplay());
        existing.setVideoQuality(updates.getVideoQuality());
        existing.setFontSize(updates.getFontSize());
        existing.setTheme(updates.getTheme());
    }

    public static double getSpeedMultiplier(ReadingPreferencesEntity.ReadingSpeed speed) {

        return switch (speed) {
            case SLOW -> 0.75;
            case NORMAL -> 1.0;
            case FAST -> 1.25;
            case FASTER -> 1.5;
        };
    }

    public static String getVideoQualityResolution(ReadingPreferencesEntity.VideoQuality quality) {

        return switch (quality) {
            case LOW -> "480p";
            case MEDIUM -> "720p";
            case HIGH -> "1080p";
            case AUTO -> "auto";
        };
    }

    public static String getLanguageCode(ReadingPreferencesEntity.Language language) {

        return switch (language) {
            case ENGLISH -> "en";
            case TURKISH -> "tr";
            case SPANISH -> "es";
            case FRENCH -> "fr";
            case GERMAN -> "de";
        };
    }
}
