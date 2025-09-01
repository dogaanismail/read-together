package org.readtogether.feedback.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequestListRequest {

    private FeatureRequestCategory category;

    private FeatureRequestStatus status;

    private String sortBy;

    private Pageable pageable;

}