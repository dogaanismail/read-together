package org.readtogether.acceptance.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * RestAssured-based API client for acceptance tests.
 * Provides a configured HTTP client with authentication helpers.
 */
@Slf4j
public class ApiClient {
    
    private static RequestSpecification baseRequestSpec;
    private static String currentAccessToken;
    private static Response lastResponse;
    
    static {
        configureRestAssured();
    }
    
    /**
     * Configure RestAssured with base settings.
     */
    private static void configureRestAssured() {
        RestAssured.baseURI = Env.getBaseUrl();
        RestAssured.basePath = Env.getApiBasePath();
        
        // Configure object mapper for JSON serialization
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .defaultObjectMapperType(ObjectMapperType.JACKSON_2));
        
        // Base request specification
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
        
        // Add logging if debug mode is enabled
        if (Env.isDebugMode()) {
            builder.log(LogDetail.ALL);
        }
        
        baseRequestSpec = builder.build();
        
        log.info("API Client configured for base URL: {}", Env.getApiUrl());
    }
    
    /**
     * Get a basic request specification without authentication.
     */
    public static RequestSpecification getRequest() {
        return given().spec(baseRequestSpec);
    }
    
    /**
     * Get a request specification that ignores the default API base path.
     * Useful for actuator/health and other non-API endpoints.
     */
    public static RequestSpecification getRequestWithoutApiBase() {
        return given().spec(baseRequestSpec).basePath("");
    }

    /**
     * Get a request specification with Bearer token authentication.
     */
    public static RequestSpecification getAuthenticatedRequest() {
        if (currentAccessToken == null) {
            throw new IllegalStateException("No access token available. Please login first.");
        }
        
        return getRequest()
                .header("Authorization", "Bearer " + currentAccessToken);
    }
    
    /**
     * Get a request specification with Bearer token authentication using provided token.
     */
    public static RequestSpecification getAuthenticatedRequest(String accessToken) {
        return getRequest()
                .header("Authorization", "Bearer " + accessToken);
    }
    
    /**
     * Set the current access token for authenticated requests.
     */
    public static void setAccessToken(String accessToken) {
        currentAccessToken = accessToken;
        log.debug("Access token set for future authenticated requests");
    }
    
    /**
     * Clear the current access token.
     */
    public static void clearAccessToken() {
        currentAccessToken = null;
        log.debug("Access token cleared");
    }
    
    /**
     * Get the last response received from an API call.
     */
    public static Response getLastResponse() {
        if (lastResponse == null) {
            throw new IllegalStateException("No response available. Make an API call first.");
        }
        return lastResponse;
    }
    
    /**
     * Store the response for later access.
     */
    private static Response storeResponse(Response response) {
        lastResponse = response;
        return response;
    }
    
    /**
     * POST request with JSON body.
     */
    public static Response post(String endpoint, Object body) {
        return storeResponse(getRequest()
                .body(body)
                .when()
                .post(endpoint));
    }
    
    /**
     * Authenticated POST request with JSON body.
     */
    public static Response postAuthenticated(String endpoint, Object body) {
        return storeResponse(getAuthenticatedRequest()
                .body(body)
                .when()
                .post(endpoint));
    }
    
    /**
     * GET request.
     */
    public static Response get(String endpoint) {
        return storeResponse(getRequest()
                .when()
                .get(endpoint));
    }

    /**
     * GET request without an API base path.
     */
    public static Response getWithoutApiBase(String endpoint) {
        return storeResponse(getRequestWithoutApiBase()
                .when()
                .get(endpoint));
    }

    /**
     * Authenticated GET request.
     */
    public static Response getAuthenticated(String endpoint) {
        return storeResponse(getAuthenticatedRequest()
                .when()
                .get(endpoint));
    }
    
    /**
     * GET request with query parameters.
     */
    public static Response get(String endpoint, Map<String, Object> queryParams) {
        return storeResponse(getRequest()
                .queryParams(queryParams)
                .when()
                .get(endpoint));
    }
    
    /**
     * Authenticated GET request with query parameters.
     */
    public static Response getAuthenticated(String endpoint, Map<String, Object> queryParams) {
        return storeResponse(getAuthenticatedRequest()
                .queryParams(queryParams)
                .when()
                .get(endpoint));
    }
    
    /**
     * PUT request with JSON body.
     */
    public static Response put(String endpoint, Object body) {
        return storeResponse(getRequest()
                .body(body)
                .when()
                .put(endpoint));
    }
    
    /**
     * Authenticated PUT request with JSON body.
     */
    public static Response putAuthenticated(String endpoint, Object body) {
        return storeResponse(getAuthenticatedRequest()
                .body(body)
                .when()
                .put(endpoint));
    }
    
    /**
     * DELETE request.
     */
    public static Response delete(String endpoint) {
        return storeResponse(getRequest()
                .when()
                .delete(endpoint));
    }
    
    /**
     * Authenticated DELETE request.
     */
    public static Response deleteAuthenticated(String endpoint) {
        return storeResponse(getAuthenticatedRequest()
                .when()
                .delete(endpoint));
    }
}