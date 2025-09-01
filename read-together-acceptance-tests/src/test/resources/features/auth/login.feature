Feature: User Login
  As a registered user
  I want to log in to the application
  So that I can access protected features

  Background:
    Given the application is running
    And the database is clean

  Scenario: Successful login with valid credentials
    Given a user exists with email "test.login@test.local" and password "Password1!"
    When I login with email "test.login@test.local" and password "Password1!"
    Then I should receive a successful login response
    And I should receive an access token
    And I should receive a refresh token
    And the access token should be structurally valid
    And the refresh token should be structurally valid

  Scenario: Failed login with invalid password
    Given a user exists with email "test.login.fail@test.local" and password "Password1!"
    When I login with email "test.login.fail@test.local" and password "WrongPassword"
    Then I should receive a 401 unauthorized response
    And the response should contain an error message
    And I should not receive any tokens

  Scenario: Failed login with non-existent user
    When I login with email "nonexistent@test.local" and password "Password1!"
    Then I should receive a 401 unauthorized response
    And the response should contain an error message
    And I should not receive any tokens

  Scenario: Failed login with malformed email
    When I login with email "invalid-email" and password "Password1!"
    Then I should receive a 400 bad request response
    And the response should contain validation errors

  Scenario: Failed login with empty credentials
    When I login with email "" and password ""
    Then I should receive a 400 bad request response
    And the response should contain validation errors