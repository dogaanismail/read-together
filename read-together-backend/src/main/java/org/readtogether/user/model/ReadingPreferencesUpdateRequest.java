package org.readtogether.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.user.entity.ReadingPreferencesEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPreferencesUpdateRequest {

    private ReadingPreferencesEntity.Language defaultLanguage;
    private ReadingPreferencesEntity.ReadingSpeed readingSpeed;
    private Boolean subtitlesEnabled;
    private Boolean autoplay;
    private ReadingPreferencesEntity.VideoQuality videoQuality;
    private ReadingPreferencesEntity.FontSize fontSize;
    private ReadingPreferencesEntity.Theme theme;
}
