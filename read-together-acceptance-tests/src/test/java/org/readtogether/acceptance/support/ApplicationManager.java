package org.readtogether.acceptance.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.ReadTogetherApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Manages the Spring Boot application lifecycle for acceptance tests.
 * Handles starting and stopping the backend application in embedded mode.
 */
@Slf4j
@UtilityClass
public class ApplicationManager {
    
    private static ConfigurableApplicationContext applicationContext;
    
    /**
     * Start the Spring Boot application for embedded mode.
     */
    public static void startApplication() {
        if (!Env.isEmbeddedMode()) {
            log.debug("Not in embedded mode, skipping application startup");
            return;
        }
        
        if (applicationContext != null && applicationContext.isRunning()) {
            log.debug("Application already running");
            return;
        }
        
        log.info("Starting Spring Boot application for embedded mode...");
        
        try {
            // Set system properties for the application to use testcontainer database
            System.setProperty("server.port", "7031");
            System.setProperty("management.server.port", "7032");
            
            // Create SpringApplication
            SpringApplication app = new SpringApplication(ReadTogetherApplication.class);
            
            // Set active profiles for testing
            app.setAdditionalProfiles("test");
            
            // Disable banner for cleaner test output
            app.setBannerMode(org.springframework.boot.Banner.Mode.OFF);
            
            // Start the application
            applicationContext = app.run();
            
            log.info("Spring Boot application started successfully on port 7031");
            
        } catch (Exception e) {
            log.error("Failed to start Spring Boot application", e);
            throw new RuntimeException("Could not start application for tests", e);
        }
    }
    
    /**
     * Stop the Spring Boot application.
     */
    public static void stopApplication() {
        if (applicationContext != null && applicationContext.isRunning()) {
            log.info("Stopping Spring Boot application...");
            try {
                applicationContext.close();
                applicationContext = null;
                log.info("Spring Boot application stopped successfully");
            } catch (Exception e) {
                log.warn("Error stopping application: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Check if the application is running.
     */
    public static boolean isApplicationRunning() {
        return applicationContext != null && applicationContext.isRunning();
    }
}