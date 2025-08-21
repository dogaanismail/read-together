package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.model.ReadingPreferencesUpdateRequest;

import java.util.UUID;

@UtilityClass
public class ReadingPreferencesFactory {

    public static ReadingPreferencesEntity createDefaultPreferences(UUID userId) {

        return ReadingPreferencesEntity.builder()
                .userId(userId)
                .build();
    }

    public static ReadingPreferencesEntity createFromUpdateRequest(
            UUID userId,
            ReadingPreferencesEntity updateRequest) {

        return ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(updateRequest.getDefaultLanguage())
                .readingSpeed(updateRequest.getReadingSpeed())
                .subtitlesEnabled(updateRequest.isSubtitlesEnabled())
                .autoplay(updateRequest.isAutoplay())
                .videoQuality(updateRequest.getVideoQuality())
                .fontSize(updateRequest.getFontSize())
                .theme(updateRequest.getTheme())
                .build();
    }

    public static ReadingPreferencesEntity createFromDto(
            UUID userId,
            ReadingPreferencesUpdateRequest dto) {

        return ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(dto.getDefaultLanguage() != null ? dto.getDefaultLanguage() : ReadingPreferencesEntity.Language.ENGLISH)
                .readingSpeed(dto.getReadingSpeed() != null ? dto.getReadingSpeed() : ReadingPreferencesEntity.ReadingSpeed.NORMAL)
                .subtitlesEnabled(dto.getSubtitlesEnabled() != null ? dto.getSubtitlesEnabled() : true)
                .autoplay(dto.getAutoplay() != null ? dto.getAutoplay() : false)
                .videoQuality(dto.getVideoQuality() != null ? dto.getVideoQuality() : ReadingPreferencesEntity.VideoQuality.HIGH)
                .fontSize(dto.getFontSize() != null ? dto.getFontSize() : ReadingPreferencesEntity.FontSize.MEDIUM)
                .theme(dto.getTheme() != null ? dto.getTheme() : ReadingPreferencesEntity.Theme.LIGHT)
                .build();
    }
}
