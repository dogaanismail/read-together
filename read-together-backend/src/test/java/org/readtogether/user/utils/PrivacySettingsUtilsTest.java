package org.readtogether.user.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.user.common.enums.ProfileVisibility;
import org.readtogether.user.entity.PrivacySettingsEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PrivacySettingsUtils Tests")
class PrivacySettingsUtilsTest {

    @Test
    @DisplayName("Should copy all fields from updates to existing")
    void shouldCopyAllFieldsFromUpdates() {
        PrivacySettingsEntity existing = new PrivacySettingsEntity();
        existing.setProfileVisibility(ProfileVisibility.PUBLIC);
        existing.setShowEmail(false);
        existing.setShowOnlineStatus(true);
        existing.setAllowMessages(true);
        existing.setShowReadingSessions(true);
        existing.setSearchable(true);

        PrivacySettingsEntity updates = new PrivacySettingsEntity();
        updates.setProfileVisibility(ProfileVisibility.PRIVATE);
        updates.setShowEmail(true);
        updates.setShowOnlineStatus(false);
        updates.setAllowMessages(false);
        updates.setShowReadingSessions(false);
        updates.setSearchable(false);

        PrivacySettingsUtils.applyUpdates(existing, updates);

        assertThat(existing.getProfileVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
        assertThat(existing.isShowEmail()).isTrue();
        assertThat(existing.isShowOnlineStatus()).isFalse();
        assertThat(existing.isAllowMessages()).isFalse();
        assertThat(existing.isShowReadingSessions()).isFalse();
        assertThat(existing.isSearchable()).isFalse();
    }

    @Test
    @DisplayName("Should allow access when visibility is PUBLIC")
    void shouldAllowAccessWhenVisibilityIsPublic() {
        PrivacySettingsEntity settings = new PrivacySettingsEntity();
        settings.setProfileVisibility(ProfileVisibility.PUBLIC);

        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, false, false)).isTrue();
        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, true, false)).isTrue();
    }

    @Test
    @DisplayName("Should allow access for followers when visibility is FOLLOWERS")
    void shouldAllowAccessForFollowersWhenVisibilityIsFollowers() {
        PrivacySettingsEntity settings = new PrivacySettingsEntity();
        settings.setProfileVisibility(ProfileVisibility.FOLLOWERS);

        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, false, false)).isFalse();
        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, true, false)).isTrue();
    }

    @Test
    @DisplayName("Should deny access when visibility is PRIVATE and requester is not owner")
    void shouldDenyAccessWhenVisibilityIsPrivateAndNotOwner() {
        PrivacySettingsEntity settings = new PrivacySettingsEntity();
        settings.setProfileVisibility(ProfileVisibility.PRIVATE);

        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, false, false)).isFalse();
        assertThat(PrivacySettingsUtils.isProfileAccessible(settings, true, false)).isFalse();
    }

    @Test
    @DisplayName("Should not allow sending messages when disabled regardless of access")
    void shouldNotAllowSendingMessagesWhenDisabled() {
        PrivacySettingsEntity settings = new PrivacySettingsEntity();
        settings.setAllowMessages(false);
        settings.setProfileVisibility(ProfileVisibility.PUBLIC);

        assertThat(PrivacySettingsUtils.canSendMessage(settings, false, false)).isFalse();
        assertThat(PrivacySettingsUtils.canSendMessage(settings, true, true)).isFalse();
    }

    @Test
    @DisplayName("Should allow sending messages only when enabled and profile accessible")
    void shouldAllowSendingMessagesOnlyWhenEnabledAndAccessible() {
        PrivacySettingsEntity settings = new PrivacySettingsEntity();
        settings.setAllowMessages(true);
        settings.setProfileVisibility(ProfileVisibility.FOLLOWERS);

        assertThat(PrivacySettingsUtils.canSendMessage(settings, false, false)).isFalse();
        assertThat(PrivacySettingsUtils.canSendMessage(settings, true, false)).isTrue();
        assertThat(PrivacySettingsUtils.canSendMessage(settings, false, true)).isTrue();
    }
}
