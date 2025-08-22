package org.readtogether.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsResponse {

    private String profileVisibility; // lowercase: public|followers|private
    private boolean showEmail;
    private boolean showOnlineStatus;
    private boolean allowMessages;
    private boolean showReadingSessions;
    private boolean searchable;
}
