Feature: Access Protected Endpoints
  As a user
  I want to access protected endpoints with a valid token
  So that I can use authenticated features of the application

  Background:
    Given the application is running
    And the database is clean

  Scenario: Access protected endpoint with valid access token
    Given a user exists with email "test.protected@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I access the protected endpoint "/users/current-user" with the access token
    Then I should receive a 200 OK response
    And the response should contain my user information

  Scenario: Access protected endpoint without access token
    When I access the protected endpoint "/users/current-user" without authentication
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Access protected endpoint with invalid access token
    When I access the protected endpoint "/users/current-user" with invalid token "invalid.access.token"
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Access protected endpoint with expired access token
    Given I have an expired access token
    When I access the protected endpoint "/users/current-user" with the expired token
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error about expired token

  Scenario: Access protected endpoint with malformed access token
    When I access the protected endpoint "/users/current-user" with malformed token "malformed-token"
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Access multiple protected endpoints with same valid token
    Given a user exists with email "test.multiple@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I access the protected endpoint "/users/current-user" with the access token
    Then I should receive a 200 OK response
    When I access the protected endpoint "/privacy-settings" with the access token
    Then I should receive a 200 OK response
    When I access the protected endpoint "/reading-preferences" with the access token
    Then I should receive a 200 OK response

  Scenario: Access admin-only endpoint with user token
    Given a user exists with email "test.user.admin@test.local" and password "Password1!"
    And I have logged in as a regular user and obtained an access token
    When I access the admin endpoint "/admin/users" with the access token
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error