package org.readtogether.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.user.common.enums.ProfileVisibility;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsUpdateRequest {

    private ProfileVisibility profileVisibility;
    private Boolean showEmail;
    private Boolean showOnlineStatus;
    private Boolean allowMessages;
    private Boolean showReadingSessions;
    private Boolean searchable;
}
