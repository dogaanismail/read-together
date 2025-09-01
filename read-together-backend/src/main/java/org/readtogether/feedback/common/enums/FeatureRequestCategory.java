package org.readtogether.feedback.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeatureRequestCategory {

    RECORDING_AUDIO("Recording & Audio"),
    COMMUNITY_FEATURES("Community Features"),
    UI_UX_IMPROVEMENTS("UI/UX Improvements"),
    AI_ANALYTICS("AI & Analytics"),
    ACCESSIBILITY("Accessibility"),
    MOBILE_EXPERIENCE("Mobile Experience"),
    OTHER("Other");

    private final String displayName;

}