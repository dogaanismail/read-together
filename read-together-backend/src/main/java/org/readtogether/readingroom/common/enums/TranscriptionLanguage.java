package org.readtogether.readingroom.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TranscriptionLanguage {
    ENGLISH("English"),
    FRENCH("French"),
    SPANISH("Spanish"),
    GERMAN("German"),
    ITALIAN("Italian"),
    PORTUGUESE("Portuguese"),
    DUTCH("Dutch"),
    RUSSIAN("Russian"),
    CHINESE("Chinese"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    ARABIC("Arabic");

    private final String displayName;
}
