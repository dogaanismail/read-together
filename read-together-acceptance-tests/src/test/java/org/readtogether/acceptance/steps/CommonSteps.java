package org.readtogether.acceptance.steps;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.readtogether.acceptance.support.ApiClient;
import org.readtogether.acceptance.support.DbUtils;
import org.readtogether.acceptance.support.Env;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common step definitions and setup/teardown for acceptance tests.
 */
@Slf4j
public class CommonSteps {
    
    @Before
    public void beforeScenario() {
        log.debug("Starting scenario setup...");
        
        // Start embedded database if in embedded mode
        if (Env.isEmbeddedMode()) {
            DbUtils.startEmbeddedDatabase();
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
        
        // Stop embedded database if in embedded mode
        if (Env.isEmbeddedMode()) {
            DbUtils.stopEmbeddedDatabase();
        }
        
        log.debug("Scenario cleanup completed");
    }
    
    @Given("the application is running")
    public void the_application_is_running() {
        log.info("Verifying application is running at: {}", Env.getApiUrl());
        
        // Wait for application to be available
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> {
                    try {
                        // Try to access a health endpoint or any available endpoint
                        var response = ApiClient.get("/health");
                        return response.getStatusCode() == 200 || 
                               response.getStatusCode() == 404; // 404 is ok if health endpoint doesn't exist
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
        // Ensure database is running and accessible
        the_database_is_clean();
        
        // Seed any initial test data if needed
        DbUtils.seedTestData();
        
        log.debug("Database setup completed");
    }
}