package org.readtogether.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesResponse {

    private boolean emailNotifications;

    private boolean pushNotifications;

    private boolean sessionLikes;

    private boolean newFollowers;

    private boolean liveStreamAlerts;

    private boolean weeklyDigest;

    private boolean marketingEmails;

    private boolean uploadStatus;

    private String emailAddress;

    private String phoneNumber;

    private String pushSubscriptionEndpoint;
}
