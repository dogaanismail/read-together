package org.readtogether.acceptance.steps;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.readtogether.acceptance.support.ApiClient;
import org.readtogether.acceptance.support.ApplicationManager;
import org.readtogether.acceptance.support.DbUtils;
import org.readtogether.acceptance.support.Env;
import io.restassured.RestAssured;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common step definitions and setup/teardown for acceptance tests.
 */
@Slf4j
public class CommonSteps {
    
    private static boolean shutdownHookAdded = false;
    
    @Before
    public void beforeScenario() {
        log.debug("Starting scenario setup...");
        
        // Add shutdown hook once to clean up resources when test suite completes
        if (!shutdownHookAdded && Env.isEmbeddedMode()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Test suite completed, cleaning up resources...");
                ApplicationManager.stopApplication();
                DbUtils.stopEmbeddedDatabase();
            }));
            shutdownHookAdded = true;
        }
        
        // Start embedded database if in embedded mode
        if (Env.isEmbeddedMode()) {
            DbUtils.startEmbeddedDatabase();
            // Start the Spring Boot application with the embedded database
            ApplicationManager.startApplication();
        }
        
        // Clear any existing auth tokens from previous scenarios
        ApiClient.clearAccessToken();
        
        log.debug("Scenario setup completed");
    }
    
    @After
    public void afterScenario() {
        log.debug("Starting scenario cleanup...");
        
        // Clean up test data if in local mode
        if (Env.isLocalMode()) {
            DbUtils.cleanupTestData();
        }
        
        // Clear auth tokens
        ApiClient.clearAccessToken();
        
        // Note: We don't stop the application/database between scenarios for performance
        // They will be stopped when the test suite completes
        
        log.debug("Scenario cleanup completed");
    }
    
    @Given("the application is running")
    public void the_application_is_running() {
        log.info("Verifying application is running at: {}", Env.getBaseUrl());

        // Wait for the application to be available with longer timeout for startup
        Awaitility.await()
                .atMost(Duration.ofSeconds(90))  // Increased timeout for application startup
                .pollInterval(Duration.ofSeconds(3))  // Less frequent polling to reduce load
                .until(() -> {
                    try {
                        // Try main server health endpoint first
                        var response = ApiClient.getWithoutApiBase("/actuator/health");
                        int status = response.getStatusCode();
                        if (status == 200) {
                            log.debug("Health check successful at main server");
                            return true;
                        }
                        
                        // If main health check fails, try management server
                        if (status == 404) {
                            try {
                                // The management server runs on port 5007
                                var mgmtResponse = RestAssured.given()
                                    .baseUri("http://localhost:5007")
                                    .when()
                                    .get("/actuator/health");
                                int mgmtStatus = mgmtResponse.getStatusCode();
                                log.debug("Management server health check: {}", mgmtStatus);
                                return mgmtStatus == 200;
                            } catch (Exception mgmtEx) {
                                log.debug("Management server health check failed: {}", mgmtEx.getMessage());
                            }
                        }
                        
                        log.debug("Application health check returned status: {}", status);
                        return false;
                    } catch (Exception e) {
                        log.debug("Application not yet available: {}", e.getMessage());
                        return false;
                    }
                });
        
        log.info("Application is running and accessible");
    }
    
    @Given("the database is clean")
    public void the_database_is_clean() {
        if (Env.isLocalMode()) {
            log.debug("Cleaning database for local mode...");
            DbUtils.cleanupTestData();
        } else {
            log.debug("Using fresh database container for embedded mode");
            // In embedded mode, each scenario gets a fresh container, so no cleanup needed
        }
        
        // Verify database is accessible
        assertThat(DbUtils.isDatabaseAccessible())
                .as("Database should be accessible")
                .isTrue();
        
        log.debug("Database is clean and accessible");
    }
    
    @Given("the database is setup")
    public void the_database_is_setup() {
        // Ensure the database is running and accessible
        the_database_is_clean();
        
        // Seed any initial test data if needed
        DbUtils.seedTestData();
        
        log.debug("Database setup completed");
    }
}