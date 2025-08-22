package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.common.enums.ProfileVisibility;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;

@UtilityClass
public class PrivacySettingsRequestFixtures {

    public static PrivacySettingsUpdateRequest createFollowersTightRequest() {

        return PrivacySettingsUpdateRequest.builder()
                .profileVisibility(ProfileVisibility.FOLLOWERS)
                .showEmail(true)
                .showOnlineStatus(false)
                .allowMessages(true)
                .showReadingSessions(false)
                .searchable(false)
                .build();
    }

    public static PrivacySettingsUpdateRequest createPrivateNoMessagesRequest() {

        return PrivacySettingsUpdateRequest.builder()
                .profileVisibility(ProfileVisibility.PRIVATE)
                .showEmail(false)
                .showOnlineStatus(false)
                .allowMessages(false)
                .showReadingSessions(false)
                .searchable(false)
                .build();
    }

    public static PrivacySettingsUpdateRequest createUpdateRequest(
            ProfileVisibility visibility,
            Boolean showEmail,
            Boolean showOnlineStatus,
            Boolean allowMessages,
            Boolean showReadingSessions,
            Boolean searchable) {

        return PrivacySettingsUpdateRequest.builder()
                .profileVisibility(visibility)
                .showEmail(showEmail)
                .showOnlineStatus(showOnlineStatus)
                .allowMessages(allowMessages)
                .showReadingSessions(showReadingSessions)
                .searchable(searchable)
                .build();
    }
}

