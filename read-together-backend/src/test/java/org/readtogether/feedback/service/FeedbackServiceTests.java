package org.readtogether.feedback.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString(USER_ID.getValue())).thenReturn(TEST_USER_ID.toString());
    }

    @Test
    @DisplayName("Should submit feature request successfully")
    void shouldSubmitFeatureRequestSuccessfully() {

        // Given
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

}