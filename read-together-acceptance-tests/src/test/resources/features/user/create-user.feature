Feature: Create User
  As a new user
  I want to register for an account
  So that I can use the application

  Background:
    Given the application is running
    And the database is clean

  Scenario: Successfully create a new user with valid data
    When I register a new user with the following details:
      | email      | test.create@test.local |
      | password   | Password1!             |
      | firstName  | John                   |
      | lastName   | Doe                    |
      | userType   | user                   |
    Then I should receive a 200 OK response
    And the response should indicate successful registration
    And I should be able to login with the created credentials

  Scenario: Successfully create a user with minimal valid data
    When I register a new user with the following details:
      | email      | test.minimal@test.local |
      | password   | Password1!              |
      | firstName  | Jane                    |
      | lastName   | Smith                   |
      | userType   | user                    |
    Then I should receive a 200 OK response
    And the response should indicate successful registration

  Scenario: Fail to create user with duplicate email
    Given a user exists with email "test.duplicate@test.local"
    When I register a new user with email "test.duplicate@test.local"
    Then I should receive a 409 conflict response
    And the response should contain an error about duplicate email

  Scenario: Fail to create user with invalid email format
    When I register a new user with the following details:
      | email      | invalid-email-format   |
      | password   | Password1!             |
      | firstName  | John                   |
      | lastName   | Doe                    |
      | userType   | user                   |
    Then I should receive a 400 bad request response
    And the response should contain validation errors about email format

  Scenario: Fail to create user with weak password
    When I register a new user with the following details:
      | email      | test.weak@test.local |
      | password   | weak                 |
      | firstName  | John                 |
      | lastName   | Doe                  |
      | userType   | user                 |
    Then I should receive a 400 bad request response
    And the response should contain validation errors about password strength

  Scenario: Fail to create user with empty required fields
    When I register a new user with the following details:
      | email      |                      |
      | password   |                      |
      | firstName  |                      |
      | lastName   |                      |
      | userType   | user                 |
    Then I should receive a 400 bad request response
    And the response should contain validation errors about required fields

  Scenario: Fail to create user with invalid user type
    When I register a new user with the following details:
      | email      | test.invalid@test.local |
      | password   | Password1!              |
      | firstName  | John                    |
      | lastName   | Doe                     |
      | userType   | invalid-type            |
    Then I should receive a 400 bad request response
    And the response should contain validation errors about user type