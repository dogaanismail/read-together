package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.model.response.ReadingPreferencesResponse;

@UtilityClass
public class ResponseFixtures {

    public static ReadingPreferencesResponse createDefaultReadingPreferencesResponse() {

        return ReadingPreferencesResponse.builder()
                .defaultLanguage("english")
                .readingSpeed("normal")
                .subtitlesEnabled(true)
                .autoplay(false)
                .quality("high")
                .fontSize("medium")
                .theme("light")
                .build();
    }

}