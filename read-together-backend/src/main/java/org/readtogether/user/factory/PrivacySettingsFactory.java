package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;

import java.util.UUID;

@UtilityClass
public class PrivacySettingsFactory {

    public static PrivacySettingsEntity createDefaultSettings(
            UUID userId) {

        return PrivacySettingsEntity.builder()
                .userId(userId)
                .build();
    }

    public static PrivacySettingsEntity createFromUpdateRequest(
            UUID userId,
            PrivacySettingsEntity updateRequest) {

        return PrivacySettingsEntity.builder()
                .userId(userId)
                .profileVisibility(updateRequest.getProfileVisibility())
                .showEmail(updateRequest.isShowEmail())
                .showOnlineStatus(updateRequest.isShowOnlineStatus())
                .allowMessages(updateRequest.isAllowMessages())
                .showReadingSessions(updateRequest.isShowReadingSessions())
                .searchable(updateRequest.isSearchable())
                .build();
    }

    public static PrivacySettingsEntity createFromDto(
            UUID userId,
            PrivacySettingsUpdateRequest dto) {

        return PrivacySettingsEntity.builder()
                .userId(userId)
                .profileVisibility(dto.getProfileVisibility())
                .showEmail(dto.getShowEmail() != null ? dto.getShowEmail() : false)
                .showOnlineStatus(dto.getShowOnlineStatus() != null ? dto.getShowOnlineStatus() : true)
                .allowMessages(dto.getAllowMessages() != null ? dto.getAllowMessages() : true)
                .showReadingSessions(dto.getShowReadingSessions() != null ? dto.getShowReadingSessions() : true)
                .searchable(dto.getSearchable() != null ? dto.getSearchable() : true)
                .build();
    }
}
