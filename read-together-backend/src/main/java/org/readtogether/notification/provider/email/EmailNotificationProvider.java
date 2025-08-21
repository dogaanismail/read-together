package org.readtogether.notification.provider.email;

import org.readtogether.notification.provider.NotificationProvider;

import java.util.UUID;

public interface EmailNotificationProvider extends NotificationProvider {

    void sendEmail(
            String toEmail,
            String subject,
            String htmlBody,
            String textBody);

    void sendEmailToUser(
            UUID userId,
            String subject,
            String htmlBody,
            String textBody);
}
