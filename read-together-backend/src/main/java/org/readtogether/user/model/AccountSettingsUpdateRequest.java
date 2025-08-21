package org.readtogether.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSettingsUpdateRequest {

    private PrivacySettingsUpdateRequest privacy;
    private ReadingPreferencesUpdateRequest reading;
    private NotificationPreferencesUpdateRequest notifications;
}
