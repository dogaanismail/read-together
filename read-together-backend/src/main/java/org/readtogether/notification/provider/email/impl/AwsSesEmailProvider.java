package org.readtogether.notification.provider.email.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.provider.email.EmailNotificationProvider;
import org.readtogether.notification.service.NotificationPreferencesService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.email.provider", havingValue = "aws-ses")
public class AwsSesEmailProvider implements EmailNotificationProvider {

    private final NotificationPreferencesService preferencesService;

    @Override
    public boolean isEnabled() {

        return true; // TODO: Check AWS SES configuration
    }

    @Override
    public void sendNotification(
            UUID userId,
            String title,
            String message,
            String metadata) {

        NotificationPreferenceEntity preferences = preferencesService.getUserPreferences(userId);

        if (preferences.getEmailAddress() != null) {
            sendEmailToUser(userId, title, message, message);
        }
    }

    @Override
    public void sendEmail(
            String toEmail,
            String subject,
            String htmlBody,
            String textBody) {

        try {
            // TODO: Implement AWS SES integration
            log.info("Sending email via AWS SES to {}: {}", toEmail, subject);

            // Example implementation:
            // sesClient.sendEmail(SendEmailRequest.builder()
            //     .destination(Destination.builder().toAddresses(toEmail).build())
            //     .message(Message.builder()
            //         .subject(Content.builder().data(subject).build())
            //         .body(Body.builder()
            //             .html(Content.builder().data(htmlBody).build())
            //             .text(Content.builder().data(textBody).build())
            //             .build())
            //         .build())
            //     .source("noreply@readtogether.com")
            //     .build());

        } catch (Exception e) {
            log.error("Failed to send email via AWS SES to {}", toEmail, e);
        }
    }

    @Override
    public void sendEmailToUser(
            UUID userId,
            String subject,
            String htmlBody,
            String textBody) {

        NotificationPreferenceEntity preferences = preferencesService.getUserPreferences(userId);

        if (preferences.getEmailAddress() != null) {
            sendEmail(preferences.getEmailAddress(), subject, htmlBody, textBody);
        } else {
            log.warn("No email address found for user {}", userId);
        }
    }
}
