package org.readtogether.feedback.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.common.enums.Priority;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "feature_request")
@Table(name = "feature_requests")
public class FeatureRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FeatureRequestCategory category;

    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private FeatureRequestStatus status = FeatureRequestStatus.SUBMITTED;

    @Column(name = "votes", nullable = false)
    @Builder.Default
    private int votes = 0;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

}