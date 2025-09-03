package org.readtogether.acceptance.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Simple test to verify that ApplicationManager can start the backend application.
 * This helps diagnose startup issues independently of Cucumber tests.
 */
@Slf4j
class ApplicationManagerTest {
    
    @BeforeAll
    static void setup() {
        System.setProperty("E2E_MODE", "embedded");
        log.info("Starting ApplicationManager test setup...");
    }
    
    @AfterAll
    static void cleanup() {
        log.info("Cleaning up ApplicationManager test...");
        ApplicationManager.stopApplication();
        DbUtils.stopEmbeddedDatabase();
    }
    
    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void canStartBackendApplication() {
        log.info("Testing backend application startup...");
        
        // Start a database first
        DbUtils.startEmbeddedDatabase();
        log.info("Database started successfully");
        
        // Start application
        ApplicationManager.startApplication();
        log.info("Application started successfully");
        
        // Verify the application is running
        assert ApplicationManager.isApplicationRunning() : "Application should be running";
        
        log.info("Backend application startup test completed successfully");
    }
}