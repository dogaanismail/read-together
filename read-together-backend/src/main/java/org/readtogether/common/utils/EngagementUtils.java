package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EngagementUtils {

    public static String formatEngagement(long viewCount, long likeCount, long commentCount) {
        long total = viewCount + likeCount + commentCount;
        if (total < 1000) {
            return String.valueOf(total);
        } else if (total < 1000000) {
            return String.format("%.1fK", total / 1000.0);
        } else {
            return String.format("%.1fM", total / 1000000.0);
        }
    }

    public static String formatEngagement(long totalEngagement) {
        if (totalEngagement < 1000) {
            return String.valueOf(totalEngagement);
        } else if (totalEngagement < 1000000) {
            return String.format("%.1fK", totalEngagement / 1000.0);
        } else {
            return String.format("%.1fM", totalEngagement / 1000000.0);
        }
    }
}
