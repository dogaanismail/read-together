package org.readtogether.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesUpdateRequest {

    private Boolean emailNotifications;

    private Boolean pushNotifications;

    private Boolean sessionLikes;

    private Boolean newFollowers;

    private Boolean liveStreamAlerts;

    private Boolean weeklyDigest;

    private Boolean marketingEmails;

    private Boolean uploadStatus;

    private String emailAddress;

    private String phoneNumber;

    private String pushSubscriptionEndpoint;

    private String pushSubscriptionKeys;
}
