Feature: Get User Profile
  As a logged-in user
  I want to retrieve my profile information
  So that I can view and manage my account details

  Background:
    Given the application is running
    And the database is clean

  Scenario: Successfully get my own profile
    Given a user exists with email "test.profile@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I get my current user profile
    Then I should receive a 200 OK response
    And the response should contain my profile information:
      | email     | test.profile@test.local |
      | firstName | John                    |
      | lastName  | Doe                     |
      | userType  | user                    |
    And the response should not contain sensitive information like password

  Scenario: Get profile of another user by ID
    Given a user exists with email "test.profile.other@test.local" and password "Password1!"
    And another user exists with email "test.target@test.local"
    And I have logged in as the first user and obtained an access token
    When I get the profile of the second user by ID
    Then I should receive a 200 OK response
    And the response should contain the target user's public profile information
    And the response should not contain sensitive information

  Scenario: Fail to get profile without authentication
    When I get my current user profile without authentication
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to get profile with invalid token
    When I get my current user profile with invalid token "invalid.token"
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to get profile with expired token
    Given I have an expired access token
    When I get my current user profile with the expired token
    Then I should receive a 401 unauthorized response
    And the response should contain an authentication error

  Scenario: Fail to get non-existent user by ID
    Given a user exists with email "test.profile.nonexist@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I get the profile of user with non-existent ID "999999"
    Then I should receive a 404 not found response
    And the response should contain an error about user not found

  Scenario: Get profile with privacy settings - public profile
    Given a user exists with email "test.public@test.local" and password "Password1!"
    And the user has public profile visibility
    And another user exists with email "test.viewer@test.local"
    And I have logged in as the viewer and obtained an access token
    When I get the profile of the public user by ID
    Then I should receive a 200 OK response
    And the response should contain the public user's information

  Scenario: Get profile with privacy settings - private profile
    Given a user exists with email "test.private@test.local" and password "Password1!"
    And the user has private profile visibility
    And another user exists with email "test.viewer.private@test.local"
    And I have logged in as the viewer and obtained an access token
    When I get the profile of the private user by ID
    Then I should receive a 403 forbidden response
    And the response should contain an error about private profile