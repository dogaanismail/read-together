package org.readtogether.feedback.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.fixtures.BugReportEntityFixtures;
import org.readtogether.feedback.fixtures.FeatureRequestEntityFixtures;
import org.readtogether.feedback.fixtures.RequestFixtures;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;
import org.readtogether.feedback.model.response.BugReportResponse;
import org.readtogether.feedback.model.response.FeatureRequestResponse;
import org.readtogether.feedback.model.response.FeedbackStatisticsResponse;
import org.readtogether.feedback.repository.BugReportRepository;
import org.readtogether.feedback.repository.FeatureRequestRepository;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.readtogether.security.common.enums.TokenClaims.USER_ID;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedbackService Tests")
class FeedbackServiceTests {

    private static final UUID TEST_USER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    @Mock
    private FeatureRequestRepository featureRequestRepository;

    @Mock
    private BugReportRepository bugReportRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        // Only set up security context for tests that need authentication
        // Individual tests will set this up as needed
    }

    private void setupAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString(USER_ID.getValue())).thenReturn(TEST_USER_ID.toString());
    }

    @Test
    @DisplayName("Should submit feature request successfully")
    void shouldSubmitFeatureRequestSuccessfully() {

        // Given
        setupAuthentication();
        FeatureRequestSubmitRequest request = RequestFixtures.createDefaultFeatureRequestSubmitRequest();
        FeatureRequestEntity savedEntity = FeatureRequestEntityFixtures.createDefaultFeatureRequestEntity();

        when(featureRequestRepository.save(any(FeatureRequestEntity.class))).thenReturn(savedEntity);

        // When
        FeatureRequestResponse response = feedbackService.submitFeatureRequest(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getCategory()).isEqualTo(request.getCategory());
        assertThat(response.getPriority()).isEqualTo(request.getPriority());

        verify(featureRequestRepository).save(any(FeatureRequestEntity.class));
    }

    @Test
    @DisplayName("Should submit bug report successfully")
    void shouldSubmitBugReportSuccessfully() {

        // Given
        setupAuthentication();
        BugReportSubmitRequest request = RequestFixtures.createDefaultBugReportSubmitRequest();
        BugReportEntity savedEntity = BugReportEntityFixtures.createDefaultBugReportEntity();

        when(bugReportRepository.save(any(BugReportEntity.class))).thenReturn(savedEntity);

        // When
        BugReportResponse response = feedbackService.submitBugReport(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getSeverity()).isEqualTo(request.getSeverity());
        assertThat(response.getStepsToReproduce()).isEqualTo(request.getStepsToReproduce());

        verify(bugReportRepository).save(any(BugReportEntity.class));
    }

    @Test
    @DisplayName("Should retrieve feature requests with no filters")
    void shouldRetrieveFeatureRequestsWithNoFilters() {

        // Given
        Page<FeatureRequestEntity> mockPage = mock(Page.class);
        when(mockPage.map(any())).thenReturn(mock(Page.class));
        when(featureRequestRepository.findAllByOrderByVotesDescCreatedAtDesc(any(Pageable.class)))
                .thenReturn(mockPage);

        Pageable pageable = mock(Pageable.class);

        // When
        Page<FeatureRequestResponse> result = feedbackService.getFeatureRequests(null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        verify(featureRequestRepository).findAllByOrderByVotesDescCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("Should retrieve feature requests with category filter")
    void shouldRetrieveFeatureRequestsWithCategoryFilter() {

        // Given
        FeatureRequestCategory category = FeatureRequestCategory.UI_UX_IMPROVEMENTS;
        Page<FeatureRequestEntity> mockPage = mock(Page.class);
        when(mockPage.map(any())).thenReturn(mock(Page.class));
        when(featureRequestRepository.findByCategoryOrderByVotesDescCreatedAtDesc(eq(category), any(Pageable.class)))
                .thenReturn(mockPage);

        Pageable pageable = mock(Pageable.class);

        // When
        Page<FeatureRequestResponse> result = feedbackService.getFeatureRequests(category, null, pageable);

        // Then
        assertThat(result).isNotNull();
        verify(featureRequestRepository).findByCategoryOrderByVotesDescCreatedAtDesc(category, pageable);
    }

    @Test
    @DisplayName("Should retrieve feature requests with status filter")
    void shouldRetrieveFeatureRequestsWithStatusFilter() {

        // Given
        FeatureRequestStatus status = FeatureRequestStatus.SUBMITTED;
        Page<FeatureRequestEntity> mockPage = mock(Page.class);
        when(mockPage.map(any())).thenReturn(mock(Page.class));
        when(featureRequestRepository.findByStatusOrderByVotesDescCreatedAtDesc(eq(status), any(Pageable.class)))
                .thenReturn(mockPage);

        Pageable pageable = mock(Pageable.class);

        // When
        Page<FeatureRequestResponse> result = feedbackService.getFeatureRequests(null, status, pageable);

        // Then
        assertThat(result).isNotNull();
        verify(featureRequestRepository).findByStatusOrderByVotesDescCreatedAtDesc(status, pageable);
    }

    @Test
    @DisplayName("Should retrieve feature requests with both category and status filters")
    void shouldRetrieveFeatureRequestsWithBothFilters() {

        // Given
        FeatureRequestCategory category = FeatureRequestCategory.UI_UX_IMPROVEMENTS;
        FeatureRequestStatus status = FeatureRequestStatus.SUBMITTED;
        Page<FeatureRequestEntity> mockPage = mock(Page.class);
        when(mockPage.map(any())).thenReturn(mock(Page.class));
        when(featureRequestRepository.findByCategoryAndStatusOrderByVotesDescCreatedAtDesc(
                eq(category), eq(status), any(Pageable.class)))
                .thenReturn(mockPage);

        Pageable pageable = mock(Pageable.class);

        // When
        Page<FeatureRequestResponse> result = feedbackService.getFeatureRequests(category, status, pageable);

        // Then
        assertThat(result).isNotNull();
        verify(featureRequestRepository).findByCategoryAndStatusOrderByVotesDescCreatedAtDesc(category, status, pageable);
    }

    @Test
    @DisplayName("Should retrieve bug reports")
    void shouldRetrieveBugReports() {

        // Given
        Page<BugReportEntity> mockPage = mock(Page.class);
        when(mockPage.map(any())).thenReturn(mock(Page.class));
        when(bugReportRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(mockPage);

        Pageable pageable = mock(Pageable.class);

        // When
        Page<BugReportResponse> result = feedbackService.getBugReports(pageable);

        // Then
        assertThat(result).isNotNull();
        verify(bugReportRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("Should vote for feature request successfully")
    void shouldVoteForFeatureRequestSuccessfully() {

        // Given
        UUID featureRequestId = UUID.randomUUID();

        // When
        feedbackService.voteForFeatureRequest(featureRequestId);

        // Then
        verify(featureRequestRepository).incrementVotes(featureRequestId);
    }

    @Test
    @DisplayName("Should get feedback statistics successfully")
    void shouldGetFeedbackStatisticsSuccessfully() {

        // Given
        when(featureRequestRepository.countTotal()).thenReturn(25);
        when(featureRequestRepository.countByStatus(FeatureRequestStatus.IMPLEMENTED)).thenReturn(10);
        when(bugReportRepository.countTotal()).thenReturn(15);
        when(bugReportRepository.countByStatus(org.readtogether.feedback.common.enums.BugReportStatus.FIXED)).thenReturn(8);
        when(bugReportRepository.countBySeverity(org.readtogether.feedback.common.enums.BugReportSeverity.CRITICAL)).thenReturn(3);
        when(bugReportRepository.countByStatus(org.readtogether.feedback.common.enums.BugReportStatus.IN_PROGRESS)).thenReturn(5);

        // When
        FeedbackStatisticsResponse result = feedbackService.getFeedbackStatistics();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalFeatureRequests()).isEqualTo(25);
        assertThat(result.getFeaturesImplemented()).isEqualTo(10);
        assertThat(result.getTotalBugReports()).isEqualTo(15);
        assertThat(result.getBugsFixed()).isEqualTo(8);
        assertThat(result.getCriticalBugs()).isEqualTo(3);
        assertThat(result.getBugsInProgress()).isEqualTo(5);

        verify(featureRequestRepository).countTotal();
        verify(featureRequestRepository).countByStatus(FeatureRequestStatus.IMPLEMENTED);
        verify(bugReportRepository).countTotal();
        verify(bugReportRepository).countByStatus(org.readtogether.feedback.common.enums.BugReportStatus.FIXED);
        verify(bugReportRepository).countBySeverity(org.readtogether.feedback.common.enums.BugReportSeverity.CRITICAL);
        verify(bugReportRepository).countByStatus(org.readtogether.feedback.common.enums.BugReportStatus.IN_PROGRESS);
    }

}