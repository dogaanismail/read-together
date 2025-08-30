package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class TimeUtils {

    public static String formatTimeAgo(
            Instant dateTime) {

        Instant now = Instant.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 60) {
            return minutes + "m ago";
        } else if (minutes < 1440) {
            return (minutes / 60) + "h ago";
        } else {
            return (minutes / 1440) + "d ago";
        }
    }

    public static String formatDuration(
            Integer seconds) {

        if (seconds == null || seconds < 0) {
            return "00:00";
        }

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
