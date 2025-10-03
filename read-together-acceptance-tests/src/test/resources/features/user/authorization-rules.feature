Feature: Authorization Rules
  As the system
  I want to enforce proper authorization rules
  So that users can only access resources they are permitted to

  Background:
    Given the application is running
    And the database is clean

  Scenario: Regular user cannot access another user's private information
    Given a user exists with email "test.user1@test.local" and password "Password1!"
    And another user exists with email "test.user2@test.local" and password "Password1!"
    And I have logged in as the first user and obtained an access token
    When I attempt to access the second user's private information
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error

  Scenario: User can only update their own profile
    Given a user exists with email "test.owner@test.local" and password "Password1!"
    And another user exists with email "test.other@test.local" and password "Password1!"
    And I have logged in as the first user and obtained an access token
    When I attempt to update the second user's profile
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error

  Scenario: User cannot access admin-only endpoints
    Given a user exists with email "test.regular@test.local" and password "Password1!"
    And I have logged in as a regular user and obtained an access token
    When I attempt to access admin endpoint "/admin/users"
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error

  Scenario: User cannot escalate their own privileges
    Given a user exists with email "test.escalate@test.local" and password "Password1!"
    And I have logged in and obtained an access token
    When I attempt to update my user type to "admin"
    Then I should receive a 400 bad request response
    And the response should contain an error about restricted field modification
    And my user type should remain "user"

  Scenario: User cannot delete another user's account
    Given a user exists with email "test.delete.owner@test.local" and password "Password1!"
    And another user exists with email "test.delete.target@test.local" and password "Password1!"
    And I have logged in as the first user and obtained an access token
    When I attempt to delete the second user's account
    Then I should receive a 403 forbidden response
    And the second user's account should still exist

  Scenario: User cannot access private reading rooms without permission
    Given a user exists with email "test.room.owner@test.local" and password "Password1!"
    And the user creates a private reading room
    And another user exists with email "test.room.outsider@test.local" and password "Password1!"
    And I have logged in as the second user and obtained an access token
    When I attempt to access the private reading room
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error

  Scenario: User can access public reading rooms
    Given a user exists with email "test.public.owner@test.local" and password "Password1!"
    And the user creates a public reading room
    And another user exists with email "test.public.visitor@test.local" and password "Password1!"
    And I have logged in as the second user and obtained an access token
    When I attempt to access the public reading room
    Then I should receive a 200 OK response
    And the response should contain the reading room information

  Scenario: User cannot modify another user's privacy settings
    Given a user exists with email "test.privacy.owner@test.local" and password "Password1!"
    And another user exists with email "test.privacy.other@test.local" and password "Password1!"
    And I have logged in as the second user and obtained an access token
    When I attempt to modify the first user's privacy settings
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error

  Scenario: User cannot access another user's notification preferences
    Given a user exists with email "test.notif.owner@test.local" and password "Password1!"
    And another user exists with email "test.notif.other@test.local" and password "Password1!"
    And I have logged in as the second user and obtained an access token
    When I attempt to access the first user's notification preferences
    Then I should receive a 403 forbidden response
    And the response should contain an authorization error