package org.readtogether.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.user.fixtures.ReadingPreferencesRequestFixtures;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;
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
@DisplayName("ReadingPreferencesController Integration Tests")
class ReadingPreferencesControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/reading-preferences should return defaults when not set")
    void shouldReturnDefaultReadingPreferences() throws Exception {
        // Given: register and login
        String email = "rp.user.default@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "RP",
                "Default"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When / Then
        mockMvc.perform(get("/api/v1/reading-preferences")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultLanguage").value("english"))
                .andExpect(jsonPath("$.readingSpeed").value("normal"))
                .andExpect(jsonPath("$.subtitlesEnabled").value(true))
                .andExpect(jsonPath("$.autoplay").value(false))
                .andExpect(jsonPath("$.quality").value("high"))
                .andExpect(jsonPath("$.fontSize").value("medium"))
                .andExpect(jsonPath("$.theme").value("light"));
    }

    @Test
    @DisplayName("PUT /api/v1/reading-preferences should update and persist preferences")
    void shouldUpdateReadingPreferences() throws Exception {
        // Given: register and login
        String email = "rp.user.update@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "RP",
                "Update"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        ReadingPreferencesUpdateRequest update = ReadingPreferencesRequestFixtures.createFastLargeDarkSpanish();

        // When: update
        mockMvc.perform(put("/api/v1/reading-preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultLanguage").value("spanish"))
                .andExpect(jsonPath("$.readingSpeed").value("fast"))
                .andExpect(jsonPath("$.subtitlesEnabled").value(false))
                .andExpect(jsonPath("$.autoplay").value(true))
                .andExpect(jsonPath("$.quality").value("medium"))
                .andExpect(jsonPath("$.fontSize").value("large"))
                .andExpect(jsonPath("$.theme").value("dark"));

        // Then: fetch again to verify persistence
        mockMvc.perform(get("/api/v1/reading-preferences")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultLanguage").value("spanish"))
                .andExpect(jsonPath("$.readingSpeed").value("fast"))
                .andExpect(jsonPath("$.subtitlesEnabled").value(false))
                .andExpect(jsonPath("$.autoplay").value(true))
                .andExpect(jsonPath("$.quality").value("medium"))
                .andExpect(jsonPath("$.fontSize").value("large"))
                .andExpect(jsonPath("$.theme").value("dark"));
    }

    @Test
    @DisplayName("GET /api/v1/reading-preferences/playback-settings should reflect current preferences")
    void shouldReturnPlaybackSettingsFromPreferences() throws Exception {
        // Given: register and login, then update
        String email = "rp.user.playback@test.local";
        String password = "Password1!";

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(email,
                password,
                "RP",
                "Playback"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        ReadingPreferencesUpdateRequest update = ReadingPreferencesRequestFixtures.createFastLargeDarkSpanish();
        mockMvc.perform(put("/api/v1/reading-preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        // When / Then
        mockMvc.perform(get("/api/v1/reading-preferences/playback-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.speedMultiplier").value(1.25))
                .andExpect(jsonPath("$.videoQuality").value("720p"))
                .andExpect(jsonPath("$.languageCode").value("es"))
                .andExpect(jsonPath("$.subtitlesEnabled").value(false))
                .andExpect(jsonPath("$.autoplay").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/reading-preferences without auth should return 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/v1/reading-preferences"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/reading-preferences/playback-settings"))
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

