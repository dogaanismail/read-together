package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.model.response.ReadingPreferencesResponse;

@UtilityClass
public class ReadingPreferencesResponseFactory {

    public static ReadingPreferencesResponse createFromEntity(ReadingPreferencesEntity entity) {

        String language = entity.getDefaultLanguage().name().toLowerCase();
        String speed = entity.getReadingSpeed().name().toLowerCase();
        String quality = entity.getVideoQuality().name().toLowerCase();
        String fontSize = entity.getFontSize().name().toLowerCase().replace('_', '-');
        String theme = entity.getTheme().name().toLowerCase();

        return ReadingPreferencesResponse.builder()
                .defaultLanguage(language)
                .readingSpeed(speed)
                .subtitlesEnabled(entity.isSubtitlesEnabled())
                .autoplay(entity.isAutoplay())
                .quality(quality)
                .fontSize(fontSize)
                .theme(theme)
                .build();
    }
}
