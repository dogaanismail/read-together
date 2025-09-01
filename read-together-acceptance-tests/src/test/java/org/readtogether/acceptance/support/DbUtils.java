package org.readtogether.acceptance.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database utilities for acceptance tests.
 * Handles database setup, cleanup, and Testcontainers management.
 */
@Slf4j
@UtilityClass
public class DbUtils {
    
    private static PostgreSQLContainer<?> postgresContainer;
    
    /**
     * Start a PostgreSQL Testcontainer for embedded mode.
     */
    public static void startEmbeddedDatabase() {
        if (!Env.isEmbeddedMode()) {
            log.debug("Not in embedded mode, skipping Testcontainer startup");
            return;
        }
        
        if (postgresContainer != null && postgresContainer.isRunning()) {
            log.debug("PostgreSQL container already running");
            return;
        }
        
        log.info("Starting PostgreSQL Testcontainer for embedded mode...");
        
        postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("read-together-app-db")
                .withUsername("default")
                .withPassword("default")
                .withReuse(false); // Don't reuse containers between test runs
        
        postgresContainer.start();
        
        log.info("PostgreSQL Testcontainer started at: {}", postgresContainer.getJdbcUrl());
        
        // Set system properties for the backend to use if running in-process
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }
    
    /**
     * Stop PostgreSQL Testcontainer.
     */
    public static void stopEmbeddedDatabase() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            log.info("Stopping PostgreSQL Testcontainer...");
            postgresContainer.stop();
            postgresContainer = null;
        }
    }
    
    /**
     * Get database connection for direct database operations.
     */
    public static Connection getDatabaseConnection() throws SQLException {
        String jdbcUrl;
        String username;
        String password;
        
        if (Env.isEmbeddedMode() && postgresContainer != null) {
            jdbcUrl = postgresContainer.getJdbcUrl();
            username = postgresContainer.getUsername();
            password = postgresContainer.getPassword();
        } else {
            // Local mode - use a default local database
            jdbcUrl = "jdbc:postgresql://localhost:5434/read-together-app-db";
            username = "default";
            password = "default";
        }
        
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    
    /**
     * Clean up test data between scenarios (for local mode).
     */
    public static void cleanupTestData() {
        if (!Env.isLocalMode()) {
            log.debug("Not in local mode, skipping cleanup");
            return;
        }
        
        log.debug("Cleaning up test data...");
        
        try (Connection connection = getDatabaseConnection();
             Statement statement = connection.createStatement()) {
            
            // Disable foreign key checks temporarily
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Truncate tables in dependency order (children first)
            String[] tablesToTruncate = {
                "invalid_tokens",
                "user_reading_preferences", 
                "user_privacy_settings",
                "user_notification_preferences",
                "reading_room_participants",
                "reading_room_settings",
                "reading_rooms",
                "users"
            };
            
            for (String table : tablesToTruncate) {
                try {
                    statement.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
                    log.debug("Truncated table: {}", table);
                } catch (SQLException e) {
                    // Table might not exist or might be empty - log but continue
                    log.debug("Could not truncate table {}: {}", table, e.getMessage());
                }
            }
            
            // Re-enable foreign key checks
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            log.debug("Test data cleanup completed");
            
        } catch (SQLException e) {
            log.warn("Failed to cleanup test data: {}", e.getMessage());
            // Don't fail the test because of cleanup issues
        }
    }
    
    /**
     * Seed initial test data if needed.
     */
    public static void seedTestData() {
        log.debug("Seeding test data if needed...");
        
        // For now, we rely on Liquibase migrations to set up the schema
        // If specific test data is needed, it can be added here
        
        log.debug("Test data seeding completed");
    }
    
    /**
     * Check if a database is accessible.
     */
    public static boolean isDatabaseAccessible() {
        try (Connection connection = getDatabaseConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            log.debug("Database not accessible: {}", e.getMessage());
            return false;
        }
    }
}