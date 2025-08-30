package org.readtogether.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.notification.model.NotificationPreferencesResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSettingsResponse {

    private PrivacySettingsResponse privacy;

    private ReadingPreferencesResponse reading;

    private NotificationPreferencesResponse notifications;
}
