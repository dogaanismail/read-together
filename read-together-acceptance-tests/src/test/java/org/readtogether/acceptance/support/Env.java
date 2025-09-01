package org.readtogether.acceptance.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Environment configuration utility for acceptance tests.
 * Handles switching between local and embedded test modes.
 */
@Slf4j
@UtilityClass
public class Env {
    
    public static final String DEFAULT_BASE_URL = "http://localhost:5006";
    public static final String DEFAULT_MODE = "embedded";
    
    /**
     * Get the base URL for API calls.
     * Defaults to localhost:5006 if not specified.
     */
    public static String getBaseUrl() {
        String baseUrl = System.getProperty("E2E_BASE_URL", DEFAULT_BASE_URL);
        log.debug("Using base URL: {}", baseUrl);
        return baseUrl;
    }
    
    /**
     * Get the test execution mode.
     * @return "local" or "embedded"
     */
    public static String getMode() {
        String mode = System.getProperty("E2E_MODE", DEFAULT_MODE);
        log.debug("Using test mode: {}", mode);
        return mode;
    }
    
    /**
     * Check if running in local mode (assumes backend is already running).
     */
    public static boolean isLocalMode() {
        return "local".equalsIgnoreCase(getMode());
    }
    
    /**
     * Check if running in embedded mode (uses Testcontainers).
     */
    public static boolean isEmbeddedMode() {
        return "embedded".equalsIgnoreCase(getMode());
    }
    
    /**
     * Check if debug mode is enabled for verbose logging.
     */
    public static boolean isDebugMode() {
        return Boolean.parseBoolean(System.getProperty("E2E_DEBUG", "false"));
    }
    
    /**
     * Get API base path.
     */
    public static String getApiBasePath() {
        return "/api/v1";
    }
    
    /**
     * Get full API URL by combining base URL and API path.
     */
    public static String getApiUrl() {
        return getBaseUrl() + getApiBasePath();
    }
}