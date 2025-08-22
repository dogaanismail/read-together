package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.common.enums.ProfileVisibility;

import java.util.UUID;

import static org.readtogether.user.fixtures.UserEntityFixtures.DEFAULT_USER_ID;

@UtilityClass
public class PrivacySettingsFixtures {

    public static PrivacySettingsEntity createCustomPrivacySettingsEntity() {

        return PrivacySettingsEntity.builder()
                .id(UUID.fromString("660e8400-e29b-41d4-a716-446655440003"))
                .userId(DEFAULT_USER_ID)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .showEmail(true)
                .showOnlineStatus(false)
                .allowMessages(true)
                .showReadingSessions(false)
                .searchable(true)
                .build();
    }

}
