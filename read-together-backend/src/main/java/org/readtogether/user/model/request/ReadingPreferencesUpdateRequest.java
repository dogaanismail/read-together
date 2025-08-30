package org.readtogether.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;
import org.readtogether.user.entity.ReadingPreferencesEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPreferencesUpdateRequest {

    private ReadingPreferencesEntity.Language defaultLanguage;

    private ReadingSpeed readingSpeed;

    private Boolean subtitlesEnabled;

    private Boolean autoplay;

    private VideoQuality videoQuality;

    private FontSize fontSize;

    private Theme theme;
}
