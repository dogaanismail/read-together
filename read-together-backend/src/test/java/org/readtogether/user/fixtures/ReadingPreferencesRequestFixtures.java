package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;

@UtilityClass
public class ReadingPreferencesRequestFixtures {

    public static ReadingPreferencesUpdateRequest createFastLargeDarkSpanish() {

        return ReadingPreferencesUpdateRequest.builder()
                .defaultLanguage(ReadingPreferencesEntity.Language.SPANISH)
                .readingSpeed(ReadingSpeed.FAST)
                .subtitlesEnabled(false)
                .autoplay(true)
                .videoQuality(VideoQuality.MEDIUM)
                .fontSize(FontSize.LARGE)
                .theme(Theme.DARK)
                .build();
    }

}

