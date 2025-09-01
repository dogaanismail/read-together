package org.readtogether.feedback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.factory.BugReportEntityFactory;
import org.readtogether.feedback.factory.FeatureRequestEntityFactory;
import org.readtogether.feedback.factory.FeedbackStatisticsResponseFactory;
import org.readtogether.feedback.mapper.BugReportEntityToBugReportResponseMapper;
import org.readtogether.feedback.mapper.FeatureRequestEntityToFeatureRequestResponseMapper;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;
import org.readtogether.feedback.model.response.BugReportResponse;
import org.readtogether.feedback.model.response.FeatureRequestResponse;
import org.readtogether.feedback.model.response.FeedbackStatisticsResponse;
import org.readtogether.feedback.repository.BugReportRepository;
import org.readtogether.feedback.repository.FeatureRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.readtogether.security.common.enums.TokenClaims.USER_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeatureRequestRepository featureRequestRepository;
    private final BugReportRepository bugReportRepository;

    private final FeatureRequestEntityToFeatureRequestResponseMapper featureRequestResponseMapper =
            FeatureRequestEntityToFeatureRequestResponseMapper.initialize();
    private final BugReportEntityToBugReportResponseMapper bugReportResponseMapper =
            BugReportEntityToBugReportResponseMapper.initialize();

    @Transactional
    public FeatureRequestResponse submitFeatureRequest(FeatureRequestSubmitRequest request) {

        log.info("Submitting feature request with title: {}", request.getTitle());

        UUID authorId = getCurrentUserId();
        FeatureRequestEntity entity = FeatureRequestEntityFactory.createFeatureRequestEntity(request, authorId);
        FeatureRequestEntity savedEntity = featureRequestRepository.save(entity);

        log.info("Feature request submitted successfully with ID: {}", savedEntity.getId());
        return featureRequestResponseMapper.map(savedEntity);
    }

    @Transactional
    public BugReportResponse submitBugReport(BugReportSubmitRequest request) {

        log.info("Submitting bug report with title: {}", request.getTitle());

        UUID reporterId = getCurrentUserId();
        BugReportEntity entity = BugReportEntityFactory.createBugReportEntity(request, reporterId);
        BugReportEntity savedEntity = bugReportRepository.save(entity);

        log.info("Bug report submitted successfully with ID: {}", savedEntity.getId());
        return bugReportResponseMapper.map(savedEntity);
    }

    public Page<FeatureRequestResponse> getFeatureRequests(
            FeatureRequestCategory category,
            FeatureRequestStatus status,
            Pageable pageable) {

        log.info("Retrieving feature requests with category: {}, status: {}", category, status);

        Page<FeatureRequestEntity> entities;

        if (category != null && status != null) {
            entities = featureRequestRepository.findByCategoryAndStatusOrderByVotesDescCreatedAtDesc(
                    category, status, pageable
            );
        } else if (category != null) {
            entities = featureRequestRepository.findByCategoryOrderByVotesDescCreatedAtDesc(category, pageable);
        } else if (status != null) {
            entities = featureRequestRepository.findByStatusOrderByVotesDescCreatedAtDesc(status, pageable);
        } else {
            entities = featureRequestRepository.findAllByOrderByVotesDescCreatedAtDesc(pageable);
        }

        return entities.map(featureRequestResponseMapper::map);
    }

    public Page<BugReportResponse> getBugReports(Pageable pageable) {

        log.info("Retrieving bug reports");

        Page<BugReportEntity> entities = bugReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        return entities.map(bugReportResponseMapper::map);
    }

    @Transactional
    public void voteForFeatureRequest(UUID featureRequestId) {

        log.info("Adding vote for feature request ID: {}", featureRequestId);
        featureRequestRepository.incrementVotes(featureRequestId);
        log.info("Vote added successfully for feature request ID: {}", featureRequestId);
    }

    public FeedbackStatisticsResponse getFeedbackStatistics() {

        log.info("Retrieving feedback statistics");

        int totalFeatureRequests = featureRequestRepository.countTotal();
        int featuresImplemented = featureRequestRepository.countByStatus(FeatureRequestStatus.IMPLEMENTED);
        int totalBugReports = bugReportRepository.countTotal();
        int bugsFixed = bugReportRepository.countByStatus(BugReportStatus.FIXED);
        int criticalBugs = bugReportRepository.countBySeverity(BugReportSeverity.CRITICAL);
        int bugsInProgress = bugReportRepository.countByStatus(BugReportStatus.IN_PROGRESS);

        return FeedbackStatisticsResponseFactory.createFeedbackStatisticsResponse(
                totalFeatureRequests,
                featuresImplemented,
                totalBugReports,
                bugsFixed,
                criticalBugs,
                bugsInProgress
        );
    }

    private UUID getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userIdString = jwt.getClaimAsString(USER_ID.getValue());
            return UUID.fromString(userIdString);
        }
        
        throw new RuntimeException("Unable to get current user ID");
    }

}