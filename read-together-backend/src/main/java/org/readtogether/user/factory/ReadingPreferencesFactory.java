package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;

import java.util.UUID;

import static org.readtogether.user.common.enums.FontSize.MEDIUM;
import static org.readtogether.user.common.enums.ReadingSpeed.NORMAL;
import static org.readtogether.user.common.enums.Theme.LIGHT;
import static org.readtogether.user.common.enums.VideoQuality.HIGH;

@UtilityClass
public class ReadingPreferencesFactory {

    public static ReadingPreferencesEntity createDefaultPreferences(
            UUID userId) {

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
                .readingSpeed(dto.getReadingSpeed() != null ? dto.getReadingSpeed() : NORMAL)
                .subtitlesEnabled(dto.getSubtitlesEnabled() != null ? dto.getSubtitlesEnabled() : true)
                .autoplay(dto.getAutoplay() != null ? dto.getAutoplay() : false)
                .videoQuality(dto.getVideoQuality() != null ? dto.getVideoQuality() : HIGH)
                .fontSize(dto.getFontSize() != null ? dto.getFontSize() : MEDIUM)
                .theme(dto.getTheme() != null ? dto.getTheme() : LIGHT)
                .build();
    }
}
