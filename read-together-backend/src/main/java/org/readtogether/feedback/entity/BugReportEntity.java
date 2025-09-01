package org.readtogether.feedback.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bug_report")
@Table(name = "bug_reports")
public class BugReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "severity", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private BugReportSeverity severity;

    @Column(name = "steps_to_reproduce", nullable = false, columnDefinition = "TEXT")
    private String stepsToReproduce;

    @Column(name = "expected_vs_actual_behavior", nullable = false, columnDefinition = "TEXT")
    private String expectedVsActualBehavior;

    @Column(name = "browser_device_info", length = 500)
    private String browserDeviceInfo;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BugReportStatus status = BugReportStatus.SUBMITTED;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

}