package org.readtogether.feedback.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;

import java.util.UUID;

@UtilityClass
public class FeatureRequestEntityFactory {

    public static FeatureRequestEntity createFeatureRequestEntity(
            FeatureRequestSubmitRequest request,
            UUID authorId) {

        return FeatureRequestEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .authorId(authorId)
                .build();
    }

}