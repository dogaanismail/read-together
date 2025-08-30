package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.model.response.PrivacySettingsResponse;

@UtilityClass
public class PrivacySettingsResponseFactory {

    public static PrivacySettingsResponse createFromEntity(
            PrivacySettingsEntity entity) {

        return PrivacySettingsResponse.builder()
                .profileVisibility(entity.getProfileVisibility().name().toLowerCase())
                .showEmail(entity.isShowEmail())
                .showOnlineStatus(entity.isShowOnlineStatus())
                .allowMessages(entity.isAllowMessages())
                .showReadingSessions(entity.isShowReadingSessions())
                .searchable(entity.isSearchable())
                .build();
    }
}
