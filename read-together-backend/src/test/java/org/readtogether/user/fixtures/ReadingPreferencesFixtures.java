package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;

import java.util.UUID;

import static org.readtogether.user.fixtures.UserEntityFixtures.DEFAULT_USER_ID;

@UtilityClass
public class ReadingPreferencesFixtures {

    public static ReadingPreferencesEntity createDefaultReadingPreferencesEntity() {

        return ReadingPreferencesEntity.builder()
                .id(UUID.fromString("770e8400-e29b-41d4-a716-446655440000"))
                .userId(DEFAULT_USER_ID)
                .defaultLanguage(ReadingPreferencesEntity.Language.ENGLISH)
                .readingSpeed(ReadingSpeed.NORMAL)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.HIGH)
                .fontSize(FontSize.MEDIUM)
                .theme(Theme.LIGHT)
                .build();
    }

}
