package org.readtogether.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.notification.fixtures.NotificationPreferencesRequestFixtures;
import org.readtogether.user.fixtures.AccountSettingsRequestFixtures;
import org.readtogether.user.fixtures.PrivacySettingsRequestFixtures;
import org.readtogether.user.fixtures.ReadingPreferencesRequestFixtures;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("AccountSettingsController Integration Tests")
class AccountSettingsControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/account-settings should return defaults when nothing set")
    void shouldReturnDefaultAccountSettings() throws Exception {
        // Given: register and login
        String email = "as.user.default@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "AS",
                "Default"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When / Then
        mockMvc.perform(get("/api/v1/account-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                // privacy defaults
                    .andExpect(jsonPath("$.response.privacy.profileVisibility").value("public"))
                .andExpect(jsonPath("$.response.privacy.showEmail").value(false))
                .andExpect(jsonPath("$.response.privacy.showOnlineStatus").value(true))
                .andExpect(jsonPath("$.response.privacy.allowMessages").value(true))
                .andExpect(jsonPath("$.response.privacy.showReadingSessions").value(true))
                .andExpect(jsonPath("$.response.privacy.searchable").value(true))
                // reading defaults
                .andExpect(jsonPath("$.response.reading.defaultLanguage").value("english"))
                .andExpect(jsonPath("$.response.reading.readingSpeed").value("normal"))
                .andExpect(jsonPath("$.response.reading.subtitlesEnabled").value(true))
                .andExpect(jsonPath("$.response.reading.autoplay").value(false))
                .andExpect(jsonPath("$.response.reading.quality").value("high"))
                .andExpect(jsonPath("$.response.reading.fontSize").value("medium"))
                .andExpect(jsonPath("$.response.reading.theme").value("light"))
                // notification defaults
                .andExpect(jsonPath("$.response.notifications.emailNotifications").value(true))
                .andExpect(jsonPath("$.response.notifications.pushNotifications").value(true))
                .andExpect(jsonPath("$.response.notifications.sessionLikes").value(true))
                .andExpect(jsonPath("$.response.notifications.newFollowers").value(true))
                .andExpect(jsonPath("$.response.notifications.liveStreamAlerts").value(true))
                .andExpect(jsonPath("$.response.notifications.weeklyDigest").value(true))
                .andExpect(jsonPath("$.response.notifications.marketingEmails").value(false))
                .andExpect(jsonPath("$.response.notifications.uploadStatus").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/account-settings should update privacy, reading, and notification settings")
    void shouldUpdateAllAccountSettings() throws Exception {
        // Given: register and login
        String email = "as.user.update@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "AS",
                "Update"
        );

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        AccountSettingsUpdateRequest update = AccountSettingsRequestFixtures.createAll(
                PrivacySettingsRequestFixtures.createFollowersTightRequest(),
                ReadingPreferencesRequestFixtures.createFastLargeDarkSpanish(),
                NotificationPreferencesRequestFixtures.createMarketingAndDigestOff()
        );

        String token = loginAndGetAccessToken(email, password);

        // When: update all
        mockMvc.perform(put("/api/v1/account-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                // privacy
                .andExpect(jsonPath("$.response.privacy.profileVisibility").value("followers"))
                .andExpect(jsonPath("$.response.privacy.showEmail").value(true))
                .andExpect(jsonPath("$.response.privacy.showOnlineStatus").value(false))
                .andExpect(jsonPath("$.response.privacy.allowMessages").value(true))
                .andExpect(jsonPath("$.response.privacy.showReadingSessions").value(false))
                .andExpect(jsonPath("$.response.privacy.searchable").value(false))
                // reading
                .andExpect(jsonPath("$.response.reading.defaultLanguage").value("spanish"))
                .andExpect(jsonPath("$.response.reading.readingSpeed").value("fast"))
                .andExpect(jsonPath("$.response.reading.subtitlesEnabled").value(false))
                .andExpect(jsonPath("$.response.reading.autoplay").value(true))
                .andExpect(jsonPath("$.response.reading.quality").value("medium"))
                .andExpect(jsonPath("$.response.reading.fontSize").value("large"))
                .andExpect(jsonPath("$.response.reading.theme").value("dark"))
                // notifications
                .andExpect(jsonPath("$.response.notifications.newFollowers").value(false))
                .andExpect(jsonPath("$.response.notifications.liveStreamAlerts").value(false))
                .andExpect(jsonPath("$.response.notifications.weeklyDigest").value(false))
                .andExpect(jsonPath("$.response.notifications.emailAddress").value("notifications@example.com"))
                .andExpect(jsonPath("$.response.notifications.pushSubscriptionEndpoint").value("https://push.example.com/sub/123"));

        // Then: GET to verify persistence
        mockMvc.perform(get("/api/v1/account-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.privacy.profileVisibility").value("followers"))
                .andExpect(jsonPath("$.response.reading.defaultLanguage").value("spanish"))
                .andExpect(jsonPath("$.response.notifications.weeklyDigest").value(false))
                .andExpect(jsonPath("$.response.notifications.marketingEmails").value(false))
                .andExpect(jsonPath("$.response.notifications.uploadStatus").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/account-settings with notifications only should keep other modules at defaults")
    void shouldUpdateNotificationsOnly() throws Exception {
        // Given: register and login
        String email = "as.user.notifonly@test.local";
        String password = "Password1!";

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(
                email,
                password,
                "AS",
                "NotifOnly"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        AccountSettingsUpdateRequest update = AccountSettingsRequestFixtures.createNotificationsOnly(
                NotificationPreferencesRequestFixtures.createAllOff()
        );

        // When: update notifications only
        mockMvc.perform(put("/api/v1/account-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                // notifications changed
                .andExpect(jsonPath("$.response.notifications.emailNotifications").value(false))
                .andExpect(jsonPath("$.response.notifications.pushNotifications").value(false))
                // privacy still default
                .andExpect(jsonPath("$.response.privacy.profileVisibility").value("public"))
                // reading still defaults
                .andExpect(jsonPath("$.response.reading.defaultLanguage").value("english"));

        // Then: GET to verify modules
        mockMvc.perform(get("/api/v1/account-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.notifications.uploadStatus").value(false))
                .andExpect(jsonPath("$.response.privacy.showEmail").value(false))
                .andExpect(jsonPath("$.response.reading.quality").value("high"));
    }

    @Test
    @DisplayName("GET /api/v1/account-settings without auth should return 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/v1/account-settings"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetAccessToken(
            String email,
            String password) throws Exception {

        LoginRequest login = RequestFixtures.createLoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());

        return loginNode
                .path("response")
                .path("accessToken")
                .asText();
    }
}
