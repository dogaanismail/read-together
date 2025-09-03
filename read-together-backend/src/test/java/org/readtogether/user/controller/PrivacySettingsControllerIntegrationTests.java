package org.readtogether.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.PrivacySettingsRequestFixtures;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.readtogether.user.common.enums.ProfileVisibility.PRIVATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("PrivacySettingsController Integration Tests")
class PrivacySettingsControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("GET /api/v1/privacy-settings should return defaults when not set")
    void shouldReturnDefaultPrivacySettings() throws Exception {
        // Given: register and login
        String email = "ps.user.default@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "PS",
                "Default"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When / Then
        mockMvc.perform(get("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileVisibility").value("public"))
                .andExpect(jsonPath("$.showEmail").value(false))
                .andExpect(jsonPath("$.showOnlineStatus").value(true))
                .andExpect(jsonPath("$.allowMessages").value(true))
                .andExpect(jsonPath("$.showReadingSessions").value(true))
                .andExpect(jsonPath("$.searchable").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/privacy-settings should update and persist settings")
    void shouldUpdatePrivacySettings() throws Exception {
        // Given: register and login
        String email = "ps.user.update@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "PS",
                "Update"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        PrivacySettingsUpdateRequest update = PrivacySettingsRequestFixtures.createFollowersTightRequest();

        // When: update
        mockMvc.perform(put("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileVisibility").value("followers"))
                .andExpect(jsonPath("$.showEmail").value(true))
                .andExpect(jsonPath("$.showOnlineStatus").value(false))
                .andExpect(jsonPath("$.allowMessages").value(true))
                .andExpect(jsonPath("$.showReadingSessions").value(false))
                .andExpect(jsonPath("$.searchable").value(false));

        // Then: fetch again to verify persistence
        mockMvc.perform(get("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileVisibility").value("followers"))
                .andExpect(jsonPath("$.showEmail").value(true))
                .andExpect(jsonPath("$.showOnlineStatus").value(false))
                .andExpect(jsonPath("$.allowMessages").value(true))
                .andExpect(jsonPath("$.showReadingSessions").value(false))
                .andExpect(jsonPath("$.searchable").value(false));
    }

    @Test
    @DisplayName("GET can-access-profile should honor FOLLOWERS visibility and following flag")
    void shouldEvaluateProfileAccessForFollowersVisibility() throws Exception {
        // Given: target user A with FOLLOWERS visibility
        String emailA = "ps.target.followers@test.local";
        String pwdA = "Password1!";

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(emailA,
                pwdA,
                "A",
                "Target"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String tokenA = loginAndGetAccessToken(emailA, pwdA);

        PrivacySettingsUpdateRequest followersSettings = PrivacySettingsRequestFixtures.createFollowersTightRequest();

        mockMvc.perform(put("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followersSettings)))
                .andExpect(status().isOk());

        UserEntity target = userRepository.findByEmail(emailA).orElseThrow();
        UUID targetId = target.getId();

        // And: current user B
        String emailB = "ps.current.user@test.local";
        String pwdB = "Password1!";
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RequestFixtures.createRegisterRequest(emailB, pwdB, "B", "User"))))
                .andExpect(status().isOk());

        String tokenB = loginAndGetAccessToken(emailB, pwdB);

        // When / Then: following=true -> can access and can send a message
        mockMvc.perform(get("/api/v1/privacy-settings/can-access-profile")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "true")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/v1/privacy-settings/can-send-message")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "true")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // When / Then: following=false -> cannot access and cannot send a message
        mockMvc.perform(get("/api/v1/privacy-settings/can-access-profile")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "false")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(get("/api/v1/privacy-settings/can-send-message")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "false")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value(false));
    }

    @Test
    @DisplayName("GET privacy access endpoints should deny access for PRIVATE visibility with messages disabled")
    void shouldDenyAccessForPrivateNoMessages() throws Exception {
        // Given: target user A with PRIVATE and allowMessages=false
        String emailA = "ps.target.private@test.local";
        String pwdA = "Password1!";

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(
                emailA,
                pwdA,
                "A",
                "Private"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String tokenA = loginAndGetAccessToken(emailA, pwdA);

        PrivacySettingsUpdateRequest privateNoMsg = PrivacySettingsRequestFixtures.createPrivateNoMessagesRequest();

        mockMvc.perform(put("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(privateNoMsg)))
                .andExpect(status().isOk());

        UUID targetId = userRepository.findByEmail(emailA).orElseThrow().getId();

        // And: current user B
        String emailB = "ps.current.visitor@test.local";
        String pwdB = "Password1!";
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RequestFixtures.createRegisterRequest(emailB, pwdB, "B", "Visitor"))))
                .andExpect(status().isOk());
        String tokenB = loginAndGetAccessToken(emailB, pwdB);

        // When / Then: cannot access and cannot send a message regardless of the following
        mockMvc.perform(get("/api/v1/privacy-settings/can-access-profile")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "true")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        mockMvc.perform(get("/api/v1/privacy-settings/can-send-message")
                        .param("targetUserId", targetId.toString())
                        .param("isFollowing", "true")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @DisplayName("GET can-access-profile should allow owner regardless of visibility; can-send-message respects allowMessages")
    void shouldHandleOwnerAccessAndMessaging() throws Exception {
        // Given: user A with PRIVATE and allowMessages=true
        String emailA = "ps.owner@test.local";
        String pwdA = "Password1!";

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(
                emailA,
                pwdA,
                "Owner",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String tokenA = loginAndGetAccessToken(emailA, pwdA);

        PrivacySettingsUpdateRequest privateAllowMsg = PrivacySettingsRequestFixtures.createUpdateRequest(
                PRIVATE,
                false,
                false,
                true,
                false,
                false
        );

        mockMvc.perform(put("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(privateAllowMsg)))
                .andExpect(status().isOk());

        UUID selfId = userRepository.findByEmail(emailA).orElseThrow().getId();

        // When / Then: owner access is true
        mockMvc.perform(get("/api/v1/privacy-settings/can-access-profile")
                        .param("targetUserId", selfId.toString())
                        .param("isFollowing", "false")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // And: can send message true when allowMessages=true
        mockMvc.perform(get("/api/v1/privacy-settings/can-send-message")
                        .param("targetUserId", selfId.toString())
                        .param("isFollowing", "false")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        PrivacySettingsUpdateRequest privateNoMsg = PrivacySettingsRequestFixtures.createPrivateNoMessagesRequest();
        mockMvc.perform(put("/api/v1/privacy-settings")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(privateNoMsg)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/privacy-settings/can-send-message")
                        .param("targetUserId", selfId.toString())
                        .param("isFollowing", "false")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
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

