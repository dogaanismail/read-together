package org.readtogether.notification.provider.sms;

import org.readtogether.notification.provider.NotificationProvider;

import java.util.UUID;

public interface SmsNotificationProvider extends NotificationProvider {

    void sendSms(
            String phoneNumber,
            String message);

    void sendSmsToUser(
            UUID userId,
            String message);
}
