package org.readtogether.notification.provider.sms.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.provider.sms.SmsNotificationProvider;
import org.readtogether.notification.service.NotificationPreferencesService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "twilio")
public class TwilioSmsProvider implements SmsNotificationProvider {

    private final NotificationPreferencesService preferencesService;

    @Override
    public boolean isEnabled() {

        return true; // TODO: Check Twilio configuration
    }

    @Override
    public void sendNotification(
            UUID userId,
            String title,
            String message,
            String metadata) {

        sendSmsToUser(userId, title + ": " + message);
    }

    @Override
    public void sendSms(
            String phoneNumber,
            String message) {

        try {
            // TODO: Implement Twilio SMS integration
            log.info("Sending SMS via Twilio to {}: {}", phoneNumber, message);

        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio to {}", phoneNumber, e);
        }
    }

    @Override
    public void sendSmsToUser(
            UUID userId,
            String message) {

        NotificationPreferenceEntity preferences = preferencesService.getUserPreferences(userId);

        if (preferences.getPhoneNumber() != null) {
            sendSms(preferences.getPhoneNumber(), message);
        } else {
            log.warn("No phone number found for user {}", userId);
        }
    }
}
