Feature: Update User Profile
  As a logged-in user
  I want to update my profile information
  So that I can keep my account details current

  Background:
    Given the application is running
    And the database is clean

  Scenario: Successfully update profile with valid data
    Given a user exists with email "test.update@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I update my profile with the following details:
      | firstName | UpdatedFirst |
      | lastName  | UpdatedLast  |
      | bio       | Updated bio  |
    Then I should receive a 200 OK response
    And the response should contain the updated profile information
    And my profile should reflect the changes

  Scenario: Successfully update partial profile information
    Given a user exists with email "test.partial@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I update my profile with the following details:
      | firstName | PartialUpdate |
    Then I should receive a 200 OK response
    And the response should contain the updated first name
    And other profile fields should remain unchanged

  Scenario: Fail to update profile without authentication
    When I update my profile without authentication
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to update profile with invalid token
    When I update my profile with invalid token "invalid.token"
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to update profile with expired token
    Given I have an expired access token
    When I update my profile with the expired token
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to update profile with invalid data
    Given a user exists with email "test.invalid.update@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I update my profile with the following invalid details:
      | firstName | |
      | lastName  | |
    Then I should receive a 400 bad request response
    And the response should contain validation errors

  Scenario: Fail to update restricted profile fields
    Given a user exists with email "test.restricted@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I attempt to update restricted fields:
      | email    | newemail@test.local |
      | userType | admin               |
    Then I should receive a 400 bad request response
    And the response should contain an error about restricted fields
    And my original profile information should remain unchanged

  Scenario: Update profile with maximum allowed field lengths
    Given a user exists with email "test.maxlength@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I update my profile with maximum length data:
      | firstName | FirstNameThatIsExactlyFiftyCharactersLongForTesting |
      | lastName  | LastNameThatIsExactlyFiftyCharactersLongForTesting  |
      | bio       | A very long bio that contains exactly two hundred characters and is used to test the maximum allowed length for the bio field in the user profile update functionality |
    Then I should receive a 200 OK response
    And the response should contain the updated profile with long field values

  Scenario: Fail to update profile with excessive field lengths
    Given a user exists with email "test.toolong@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I update my profile with excessive length data:
      | firstName | FirstNameThatExceedsTheFiftyCharacterLimitAndShouldBeRejected |
      | bio       | A bio that is way too long and exceeds the maximum allowed character limit of two hundred characters which should result in a validation error when attempting to update the user profile with this excessively long bio text content |
    Then I should receive a 400 bad request response
    And the response should contain validation errors about field length limits