package org.readtogether.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPreferencesResponse {

    private String defaultLanguage;   // lowercase, e.g., "english"
    private String readingSpeed;      // lowercase, e.g., "normal"
    private boolean subtitlesEnabled;
    private boolean autoplay;
    private String quality;           // lowercase, e.g., "high" (frontend key is 'quality')
    private String fontSize;          // lowercase with hyphen, e.g., "extra-large"
    private String theme;             // lowercase, e.g., "light"
}
