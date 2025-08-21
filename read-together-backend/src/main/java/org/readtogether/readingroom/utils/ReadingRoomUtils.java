package org.readtogether.readingroom.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ReadingRoomUtils {

    public String generateRoomCode() {

        return UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    public String generateInvitationToken() {
        return UUID.randomUUID().toString();
    }

    public String generateShareLink(String baseUrl, String invitationToken) {
        return String.format("%s/room/join?token=%s", baseUrl, invitationToken);
    }

    public String generateQRCodeUrl(String shareLink) {

        // Generate QR code URL using QR Server API
        return String.format("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s", shareLink);
    }
}
