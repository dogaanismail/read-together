package org.readtogether.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.library.fixtures.LibraryRequestFixtures;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("BookController Integration Tests")
class BookControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/library/books should create book in user's library")
    void shouldCreateBookInUserLibrary() throws Exception {
        // Given: register and login user
        String email = "book.creator@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Creator", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        BookCreateRequest createRequest = LibraryRequestFixtures.createDefaultAddBookRequest();

        // When: create a book
        MvcResult createResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.author").value(createRequest.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(createRequest.getIsbn()))
                .andExpect(jsonPath("$.category").value(createRequest.getCategory().toString()))
                .andExpect(jsonPath("$.public").value(createRequest.isPublic()))
                .andReturn();

        // Then: verify a book is persisted (get the book ID from response)
        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createJsonNode = objectMapper.readTree(createResponse);
        String bookId = createJsonNode.get("id").asText();

        // Verify by retrieving the book
        mockMvc.perform(get("/api/v1/library/books/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()));
    }

    @Test
    @DisplayName("GET /api/v1/library/books/my-books should return user's books")
    void shouldReturnUserBooks() throws Exception {
        // Given: register and login user
        String email = "book.reader@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Reader", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Create a book first
        BookCreateRequest createRequest = LibraryRequestFixtures.createAddBookRequest(
                "9781234567890",
                "Test Book for Reading",
                "Test Author",
                LibraryRequestFixtures.createDefaultAddBookRequest().getCategory(),
                "test_source",
                "ext123",
                "https://example.com/cover.jpg"
        );

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // When: get user's books
        mockMvc.perform(get("/api/v1/library/books/my-books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$[0].author").value(createRequest.getAuthor()));
    }

    @Test
    @DisplayName("GET /api/v1/library/books/my-books/count should return user's book count")
    void shouldReturnUserBookCount() throws Exception {
        // Given: register and login user
        String email = "book.counter@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Counter", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When: get book count (should be 0 initially)
        mockMvc.perform(get("/api/v1/library/books/my-books/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // Create a book
        BookCreateRequest createRequest = LibraryRequestFixtures.createMinimalBookRequest();
        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Then: count should now be 1
        mockMvc.perform(get("/api/v1/library/books/my-books/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("GET /api/v1/library/books/public should return public books")
    @Disabled("Currently public books are not implemented")
        //TODO: Implement public books
    void shouldReturnPublicBooks() throws Exception {
        // Given: register and login user
        String email = "public.book.creator@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Public", "Creator", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Create a public book
        BookCreateRequest publicBookRequest = LibraryRequestFixtures.createPublicBookRequest();

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publicBookRequest)))
                .andExpect(status().isCreated());

        // When: get public books (no authentication required)
        mockMvc.perform(get("/api/v1/library/books/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(publicBookRequest.getTitle()))
                .andExpect(jsonPath("$[0].public").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/library/books/{id} should update book")
    void shouldUpdateBook() throws Exception {
        // Given: register and login user
        String email = "book.updater@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Updater", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Create a book first
        BookCreateRequest createRequest = LibraryRequestFixtures.createDefaultAddBookRequest();

        MvcResult createResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createJsonNode = objectMapper.readTree(createResponse);
        String bookId = createJsonNode.get("id").asText();

        // Update the book
        BookUpdateRequest updateRequest = LibraryRequestFixtures.createDefaultUpdateRequest();

        // When: update a book
        mockMvc.perform(put("/api/v1/library/books/" + bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.author").value(updateRequest.getAuthor()))
                .andExpect(jsonPath("$.public").value(updateRequest.getIsPublic()));
    }

    @Test
    @DisplayName("GET /api/v1/library/books/search/my-books should search user's books")
    void shouldSearchUserBooks() throws Exception {
        // Given: register and login user
        String email = "book.searcher@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Searcher", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Create books with searchable terms
        BookCreateRequest book1 = LibraryRequestFixtures.createAddBookRequest(
                "9781111111111",
                "Harry Potter and the Philosopher's Stone",
                "J.K. Rowling",
                LibraryRequestFixtures.createDefaultAddBookRequest().getCategory(),
                "test_source",
                "hp1",
                "https://example.com/hp1.jpg"
        );

        BookCreateRequest book2 = LibraryRequestFixtures.createAddBookRequest(
                "9782222222222",
                "The Lord of the Rings",
                "J.R.R. Tolkien",
                LibraryRequestFixtures.createDefaultAddBookRequest().getCategory(),
                "test_source",
                "lotr",
                "https://example.com/lotr.jpg"
        );

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book2)))
                .andExpect(status().isCreated());

        // When: search for "Potter"
        mockMvc.perform(get("/api/v1/library/books/search/my-books")
                        .param("query", "Potter")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        // Note: The exact search behavior depends on the repository implementation
    }

    @Test
    @DisplayName("DELETE /api/v1/library/books/{id} should delete book")
    @Disabled("Currently deleting books is not implemented")
        //TODO: Implement deleting books, and return appropriate response, implement generic global error handler
    void shouldDeleteBook() throws Exception {
        // Given: register and login user
        String email = "book.deleter@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Book", "Deleter", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Create a book first
        BookCreateRequest createRequest = LibraryRequestFixtures.createDefaultAddBookRequest();

        MvcResult createResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createJsonNode = objectMapper.readTree(createResponse);
        String bookId = createJsonNode.get("id").asText();

        // When: delete a book
        mockMvc.perform(delete("/api/v1/library/books/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Then: a verified book is deleted
        mockMvc.perform(get("/api/v1/library/books/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        BookCreateRequest createRequest = LibraryRequestFixtures.createDefaultAddBookRequest();

        // When: try to create a book without authentication
        mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());

        // When: try to get user books without authentication  
        mockMvc.perform(get("/api/v1/library/books/my-books"))
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