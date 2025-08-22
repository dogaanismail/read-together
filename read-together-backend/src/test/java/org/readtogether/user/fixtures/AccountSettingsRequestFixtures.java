package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;

@UtilityClass
public class AccountSettingsRequestFixtures {

    public static AccountSettingsUpdateRequest createAll(
            PrivacySettingsUpdateRequest privacy,
            ReadingPreferencesUpdateRequest reading,
            NotificationPreferencesUpdateRequest notifications) {

        return AccountSettingsUpdateRequest.builder()
                .privacy(privacy)
                .reading(reading)
                .notifications(notifications)
                .build();
    }

    public static AccountSettingsUpdateRequest createNotificationsOnly(
            NotificationPreferencesUpdateRequest notifications) {

        return AccountSettingsUpdateRequest.builder()
                .notifications(notifications)
                .build();
    }
}

