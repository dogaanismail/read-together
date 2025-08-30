package org.readtogether.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPreferencesResponse {

    private String defaultLanguage;

    private String readingSpeed;

    private boolean subtitlesEnabled;

    private boolean autoplay;

    private String quality;

    private String fontSize;

    private String theme;
}
