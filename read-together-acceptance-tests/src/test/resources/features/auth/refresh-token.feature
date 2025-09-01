Feature: Refresh Token
  As a user with an expired access token
  I want to use my refresh token to get a new access token
  So that I can continue using the application without re-logging in

  Background:
    Given the application is running
    And the database is clean

  Scenario: Successfully refresh access token with valid refresh token
    Given a user exists with email "test.refresh@test.local" and password "Password1!"
    And I have logged in and obtained tokens
    When I use the refresh token to get a new access token
    Then I should receive a successful token refresh response
    And I should receive a new access token
    And I should receive a new refresh token
    And the new access token should be structurally valid
    And the new refresh token should be structurally valid
    And the new access token should be different from the original

  Scenario: Failed refresh with invalid refresh token
    When I use an invalid refresh token "invalid-refresh-token"
    Then I should receive a 401 unauthorized response
    And the response should contain an error message
    And I should not receive new tokens

  Scenario: Failed refresh with expired refresh token
    Given I have an expired refresh token
    When I use the expired refresh token to get a new access token
    Then I should receive a 401 unauthorized response
    And the response should contain an error message about expired token

  Scenario: Failed refresh with malformed refresh token
    When I use a malformed refresh token "malformed.token"
    Then I should receive a 401 unauthorized response
    And the response should contain an error message

  Scenario: Failed refresh with empty refresh token
    When I use an empty refresh token
    Then I should receive a 400 bad request response
    And the response should contain validation errors