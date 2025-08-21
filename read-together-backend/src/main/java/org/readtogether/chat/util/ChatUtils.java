package org.readtogether.chat.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@UtilityClass
public class ChatUtils {

    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    public static String formatMessageTime(Instant timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIME_FORMATTER.format(timestamp);
    }

    public static String generateDirectChatRoomName(String user1Name, String user2Name) {
        return "Direct chat between " + user1Name + " and " + user2Name;
    }

    public static boolean isValidChatRoomName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 255;
    }

    public static boolean isValidMessageContent(String content) {
        return content != null && !content.trim().isEmpty() && content.length() <= 4000;
    }

    public static String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength - 3) + "...";
    }

    public static boolean isDirectChatParticipant(UUID userId1, UUID userId2, UUID participantId) {
        return participantId.equals(userId1) || participantId.equals(userId2);
    }

    public static String sanitizeContent(String content) {
        if (content == null) {
            return null;
        }
        return content.trim();
    }
}