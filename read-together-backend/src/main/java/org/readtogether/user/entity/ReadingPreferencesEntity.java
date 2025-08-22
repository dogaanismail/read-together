package org.readtogether.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;

import java.util.UUID;

import static org.readtogether.user.common.enums.FontSize.MEDIUM;
import static org.readtogether.user.common.enums.ReadingSpeed.NORMAL;
import static org.readtogether.user.common.enums.Theme.LIGHT;
import static org.readtogether.user.common.enums.VideoQuality.HIGH;
import static org.readtogether.user.entity.ReadingPreferencesEntity.Language.ENGLISH;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "reading_preferences")
@Table(name = "reading_preferences")
public class ReadingPreferencesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_language", nullable = false)
    @Builder.Default
    private Language defaultLanguage = ENGLISH;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_speed", nullable = false)
    @Builder.Default
    private ReadingSpeed readingSpeed = NORMAL;

    @Column(name = "subtitles_enabled", nullable = false)
    @Builder.Default
    private boolean subtitlesEnabled = true;

    @Column(name = "autoplay", nullable = false)
    @Builder.Default
    private boolean autoplay = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_quality", nullable = false)
    @Builder.Default
    private VideoQuality videoQuality = HIGH;

    @Enumerated(EnumType.STRING)
    @Column(name = "font_size", nullable = false)
    @Builder.Default
    private FontSize fontSize = MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    @Builder.Default
    private Theme theme = LIGHT;

    public enum Language {
        ENGLISH,
        TURKISH,
        SPANISH,
        FRENCH,
        GERMAN;

        @JsonCreator
        public static Language fromString(String value) {
            return value == null ? ENGLISH : Language.valueOf(value.trim().toUpperCase());
        }
    }

}
