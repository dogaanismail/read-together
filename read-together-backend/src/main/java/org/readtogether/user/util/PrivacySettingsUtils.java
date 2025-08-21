package org.readtogether.user.util;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.PrivacySettingsEntity;

@UtilityClass
public class PrivacySettingsUtils {

    public static void applyUpdates(
            PrivacySettingsEntity existing,
            PrivacySettingsEntity updates) {

        existing.setProfileVisibility(updates.getProfileVisibility());
        existing.setShowEmail(updates.isShowEmail());
        existing.setShowOnlineStatus(updates.isShowOnlineStatus());
        existing.setAllowMessages(updates.isAllowMessages());
        existing.setShowReadingSessions(updates.isShowReadingSessions());
        existing.setSearchable(updates.isSearchable());
    }

    public static boolean isProfileAccessible(
            PrivacySettingsEntity settings,
            boolean isFollowing,
            boolean isOwner) {

        if (isOwner) {
            return true;
        }

        return switch (settings.getProfileVisibility()) {
            case PUBLIC -> true;
            case FOLLOWERS -> isFollowing;
            case PRIVATE -> false;
        };
    }

    public static boolean canSendMessage(
            PrivacySettingsEntity settings,
            boolean isFollowing,
            boolean isOwner) {

        if (!settings.isAllowMessages()) {
            return false;
        }

        return isProfileAccessible(settings, isFollowing, isOwner);
    }
}
