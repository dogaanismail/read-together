package org.readtogether.session.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.model.SessionUpdateRequest;

@UtilityClass
public class SessionUpdateUtils {

    public static void applyUpdates(
            SessionEntity session,
            SessionUpdateRequest request) {

        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            session.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            session.setPublic(request.getIsPublic());
        }
        if (request.getTags() != null) {
            session.setTags(request.getTags());
        }
    }
}

