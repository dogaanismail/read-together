package org.readtogether.notification.provider;

import java.util.UUID;

public interface NotificationProvider {

    boolean isEnabled();

    void sendNotification(
            UUID userId,
            String title,
            String message,
            String metadata);
}
