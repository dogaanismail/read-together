package org.readtogether.feedback.repository;

import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;
import org.readtogether.feedback.entity.BugReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BugReportRepository extends JpaRepository<BugReportEntity, UUID> {

    Page<BugReportEntity> findByStatusOrderByCreatedAtDesc(
            BugReportStatus status, 
            Pageable pageable
    );

    Page<BugReportEntity> findBySeverityOrderByCreatedAtDesc(
            BugReportSeverity severity, 
            Pageable pageable
    );

    Page<BugReportEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(b) FROM bug_report b WHERE b.status = :status")
    int countByStatus(@Param("status") BugReportStatus status);

    @Query("SELECT COUNT(b) FROM bug_report b WHERE b.severity = :severity")
    int countBySeverity(@Param("severity") BugReportSeverity severity);

    @Query("SELECT COUNT(b) FROM bug_report b")
    int countTotal();

}