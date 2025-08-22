package org.readtogether.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationPreferenceType;
import org.readtogether.notification.provider.NotificationProvider;
import org.readtogether.notification.provider.email.EmailNotificationProvider;
import org.readtogether.notification.provider.sms.SmsNotificationProvider;
import org.readtogether.infrastructure.websocket.service.WebSocketNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProviderService {

    private final List<EmailNotificationProvider> emailProviders;
    private final List<SmsNotificationProvider> smsProviders;
    private final WebSocketNotificationService webSocketService;
    private final NotificationPreferencesService preferencesService;

    public void sendMultiChannelNotification(
            UUID userId,
            NotificationPreferenceType preferenceType,
            String title,
            String message,
            String metadata) {

        if (preferencesService.shouldSendEmailNotification(userId, preferenceType)) {
            sendEmailNotification(userId, title, message, metadata);
        }

        if (preferencesService.shouldSendPushNotification(userId, preferenceType)) {
            sendPushNotification(userId, title, message, metadata);
        }

        // SMS notifications are typically for critical alerts only
        if (isCriticalNotification(preferenceType)) {
            sendSmsNotification(userId, title, message, metadata);
        }
    }

    public void sendEmailNotification(
            UUID userId,
            String title,
            String message,
            String metadata) {

        emailProviders.stream()
                .filter(NotificationProvider::isEnabled)
                .findFirst()
                .ifPresentOrElse(
                        provider -> {
                            try {
                                provider.sendNotification(userId, title, message, metadata);
                                log.debug("Sent email notification to user {}: {}", userId, title);
                            } catch (Exception e) {
                                log.error("Failed to send email notification to user {}", userId, e);
                            }
                        },
                        () -> log.warn("No email provider available for user {}", userId)
                );
    }

    public void sendSmsNotification(
            UUID userId,
            String title,
            String message,
            String metadata) {

        smsProviders.stream()
                .filter(NotificationProvider::isEnabled)
                .findFirst()
                .ifPresentOrElse(
                        provider -> {
                            try {
                                provider.sendNotification(userId, title, message, metadata);
                                log.debug("Sent SMS notification to user {}: {}", userId, title);
                            } catch (Exception e) {
                                log.error("Failed to send SMS notification to user {}", userId, e);
                            }
                        },
                        () -> log.debug("No SMS provider available for user {}", userId)
                );
    }

    public void sendPushNotification(
            UUID userId,
            String title,
            String message,
            String metadata) {

        try {
            webSocketService.sendCustomNotification(userId, "GENERAL", title, message);
            log.debug("Sent push notification to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Failed to send push notification to user {}", userId, e);
        }
    }

    private boolean isCriticalNotification(NotificationPreferenceType preferenceType) {

        return preferenceType == NotificationPreferenceType.UPLOAD_STATUS;
    }

    public void sendForgotPasswordEmail(String email) {
        String title = "Reset Your Password - Read Together";
        String emailContent = getEmailContent();

        emailProviders.stream()
                .filter(NotificationProvider::isEnabled)
                .findFirst()
                .ifPresentOrElse(
                        provider -> {
                            try {
                                provider.sendEmail(email, title, emailContent, emailContent);
                                log.info("Sent forgot password email to: {}", email);
                            } catch (Exception e) {
                                log.error("Failed to send forgot password email to {}", email, e);
                                throw new RuntimeException("Failed to send forgot password email", e);
                            }
                        },
                        () -> {
                            log.warn("No email provider available for forgot password email");
                            throw new RuntimeException("Email service unavailable");
                        }
                );
    }

    private static String getEmailContent() {
        String message = "We received a request to reset your password. If you didn't make this request, please ignore this email.";
        String resetLink = "https://readtogether.app/reset-password";

        return String.format(
                """
                        Hello,
                        
                        %s
                        
                        Click the link below to reset your password:
                        %s
                        
                        This link will expire in 24 hours for security reasons.
                        
                        Best regards,
                        The Read Together Team""",
            message, resetLink
        );
    }
}
