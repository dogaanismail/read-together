package org.readtogether.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.security.repository.InvalidTokenRepository;
import org.readtogether.security.service.TokenService;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("POST /api/v1/users/register should create user and return success response")
    void shouldRegisterUser() throws Exception {
        // Given
        RegisterRequest body = RequestFixtures.createRegisterRequest(
                "john.doe+reg@test.local",
                "Password1!",
                "John",
                "Doe",
                "user"
        );

        // When / Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        Optional<UserEntity> saved = userRepository.findByEmail("john.doe+reg@test.local");
        assertThat(saved).isPresent();
        assertThat(saved.get().getPassword()).isNotBlank();
        assertThat(saved.get().getUserType().name()).isEqualTo("USER");
        assertThat(saved.get().getFirstName()).isEqualTo("John");
        assertThat(saved.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("POST /login then GET /current-user should return the logged-in user")
    void shouldReturnCurrentUserAfterLogin() throws Exception {
        // Given: register
        String email = "jane.doe+current@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                "jane.doe+current@test.local",
                "Password1!",
                "Jane",
                "Doe",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // When: login
        LoginRequest login = RequestFixtures.createLoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginResponse);
        String accessToken = loginNode.path("response").path("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // Then: current user
        mockMvc.perform(get("/api/v1/users/current-user")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.email").value(email))
                .andExpect(jsonPath("$.response.firstName").value("Jane"))
                .andExpect(jsonPath("$.response.userType").value("USER"));
    }

    @Test
    @DisplayName("GET /user?userId=... should return user by id when authorized")
    void shouldReturnUserByIdWhenAuthorized() throws Exception {
        // Given: register and login to get token and id
        String email = "alex.smith+byid@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Alex",
                "Smith",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        var login = RequestFixtures.createLoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginNode.path("response").path("accessToken").asText();

        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow();

        // When / Then
        mockMvc.perform(get("/api/v1/users/user")
                        .param("userId", userEntity.getId().toString())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.email").value(email))
                .andExpect(jsonPath("$.response.id").value(userEntity.getId().toString()));
    }

    @Test
    @DisplayName("POST /logout should invalidate both tokens and persist them")
    void shouldInvalidateTokensOnLogout() throws Exception {
        // Given: register and login
        String email = "linda.parker+logout@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Linda",
                "Parker",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        var login = RequestFixtures.createLoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = loginNode.path("response").path("accessToken").asText();
        String refreshToken = loginNode.path("response").path("refreshToken").asText();

        var logoutRequest = RequestFixtures.createTokenInvalidateRequest(accessToken, refreshToken);

        // When / Then
        mockMvc.perform(post("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        String accessTokenId = tokenService.getId(accessToken);
        String refreshTokenId = tokenService.getId(refreshToken);

        assertThat(invalidTokenRepository.findByTokenId(accessTokenId)).isPresent();
        assertThat(invalidTokenRepository.findByTokenId(refreshTokenId)).isPresent();
    }

    @Test
    @DisplayName("GET /current-user without auth should return 401 with CustomError")
    void shouldReturn401ForCurrentUserWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users/current-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }
}
