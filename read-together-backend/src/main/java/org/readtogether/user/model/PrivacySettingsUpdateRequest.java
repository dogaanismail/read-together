package org.readtogether.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.user.entity.PrivacySettingsEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsUpdateRequest {

    private PrivacySettingsEntity.ProfileVisibility profileVisibility;
    private Boolean showEmail;
    private Boolean showOnlineStatus;
    private Boolean allowMessages;
    private Boolean showReadingSessions;
    private Boolean searchable;
}
