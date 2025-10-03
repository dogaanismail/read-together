# Read Together - Acceptance Tests

This module contains BDD-style acceptance tests for the Read Together backend API using Cucumber and RestAssured.

## Overview

The acceptance tests validate end-to-end user flows through HTTP API calls, providing confidence in the application's behavior from a user perspective. Tests are written in Gherkin (BDD) format and focus on Auth and User domains.

## Test Structure

```
read-together-acceptance-tests/
├── src/test/
│   ├── java/org/readtogether/acceptance/
│   │   ├── steps/              # Step definitions
│   │   │   ├── AuthSteps.java
│   │   │   ├── UserSteps.java
│   │   │   ├── ProtectedEndpointSteps.java
│   │   │   ├── AuthorizationSteps.java
│   │   │   └── CommonSteps.java
│   │   ├── support/            # Test utilities
│   │   │   ├── ApiClient.java  # RestAssured HTTP client
│   │   │   ├── Fixtures.java   # Test data builders
│   │   │   ├── DbUtils.java    # Database utilities
│   │   │   ├── JwtUtils.java   # JWT token utilities
│   │   │   └── Env.java        # Environment configuration
│   │   └── runners/
│   │       └── CucumberTest.java # JUnit 5 test runner
│   └── resources/features/
│       ├── auth/               # Authentication scenarios
│       │   ├── login.feature
│       │   ├── refresh-token.feature
│       │   └── access-protected-endpoints.feature
│       └── user/               # User management scenarios
│           ├── create-user.feature
│           ├── get-profile.feature
│           ├── update-profile.feature
│           └── authorization-rules.feature
```

## Test Modes

### Local Mode (default for development)
Assumes the backend is already running and database is available:
```bash
# Start backend first
./gradlew :read-together-backend:bootRun --no-daemon

# Then run tests in local mode
./gradlew :read-together-acceptance-tests:testLocal
```

### Embedded Mode (default for CI)
Uses Testcontainers to start PostgreSQL automatically:
```bash
./gradlew :read-together-acceptance-tests:testEmbedded
```

## Running Tests

### Quick Start
```bash
# Run all acceptance tests (embedded mode)
./gradlew :read-together-acceptance-tests:test

# Run tests in local mode (backend must be running)
./gradlew :read-together-acceptance-tests:testLocal

# Run with debug logging
./gradlew :read-together-acceptance-tests:test -DE2E_DEBUG=true
```

### Configuration Options

Set via system properties or environment variables:

| Property | Default | Description |
|----------|---------|-------------|
| `E2E_BASE_URL` | `http://localhost:5006` | Backend API base URL |
| `E2E_MODE` | `embedded` | Test mode: `local` or `embedded` |
| `E2E_DEBUG` | `false` | Enable debug logging for HTTP requests |

Examples:
```bash
# Test against different environment
./gradlew :read-together-acceptance-tests:test -DE2E_BASE_URL=http://staging.example.com

# Run in local mode with debug
./gradlew :read-together-acceptance-tests:test -DE2E_MODE=local -DE2E_DEBUG=true
```

## Test Scenarios

### Auth Domain
- **Login**: Valid/invalid credentials, token structure validation
- **Refresh Token**: Token refresh flow, expired/invalid tokens
- **Protected Endpoints**: Access control with valid/invalid/expired tokens

### User Domain
- **Create User**: Registration with valid/invalid data, duplicate emails
- **Get Profile**: Own profile, other users, privacy settings
- **Update Profile**: Valid updates, restricted fields, validation
- **Authorization**: RBAC rules, access restrictions

## Reports

After running tests, reports are generated in:
- **HTML Report**: `build/reports/cucumber.html`
- **JSON Report**: `build/reports/cucumber.json`
- **Gradle Test Report**: `build/reports/tests/test/index.html`

View reports:
```bash
# Generate and view Cucumber report
./gradlew :read-together-acceptance-tests:cucumberReport
open build/reports/cucumber.html
```

## Development

### Adding New Tests

1. **Create Feature File**: Add `.feature` file in appropriate domain folder
2. **Write Scenarios**: Use Gherkin syntax with Given/When/Then steps
3. **Implement Steps**: Add step definitions in corresponding `*Steps.java` file
4. **Update Fixtures**: Add test data builders in `Fixtures.java` if needed

### Test Data Management

- **Fixtures**: Use `Fixtures` class for consistent test data
- **Unique Data**: Generate unique emails/IDs to avoid conflicts
- **Cleanup**: Tests clean up after themselves (local mode) or use fresh containers (embedded mode)

### Debugging

Enable debug mode to see HTTP request/response details:
```bash
./gradlew :read-together-acceptance-tests:test -DE2E_DEBUG=true
```

### Best Practices

1. **Idempotent Tests**: Each test should be independent and repeatable
2. **Realistic Data**: Use realistic test data that matches production patterns
3. **Clear Scenarios**: Write descriptive scenario names and steps
4. **Layered Assertions**: Verify both HTTP status and response content
5. **Error Cases**: Test both happy path and error scenarios

## Dependencies

- **Cucumber JVM**: BDD framework
- **RestAssured**: HTTP API testing
- **Testcontainers**: Embedded PostgreSQL for testing
- **AssertJ**: Fluent assertions
- **JUnit 5**: Test platform and runner

## Troubleshooting

### Common Issues

1. **Backend Not Running (Local Mode)**
   ```
   Connection refused to localhost:5006
   ```
   Solution: Start backend with `./gradlew :read-together-backend:bootRun`

2. **Database Connection Issues**
   ```
   Connection to PostgreSQL failed
   ```
   Solution: Ensure Docker is running for Testcontainers, or check local DB in local mode

3. **Token Expiration**
   ```
   401 Unauthorized on protected endpoints
   ```
   Solution: Check JWT token expiration settings in backend configuration

4. **Test Data Conflicts**
   ```
   Duplicate email or constraint violations
   ```
   Solution: Use unique test data generators in `Fixtures` class

### Debugging Steps

1. Check application logs: `docker logs <backend-container>`
2. Verify database state: Connect to PostgreSQL and check tables
3. Enable debug logging: `-DE2E_DEBUG=true`
4. Run single scenario: Use IDE to run specific feature/scenario
5. Check Cucumber reports: Review HTML report for detailed test results

## Integration with CI

The acceptance tests are designed to run in CI environments:

```yaml
# Example GitHub Actions configuration
- name: Run Acceptance Tests
  run: ./gradlew :read-together-acceptance-tests:test --no-daemon
  env:
    E2E_MODE: embedded
    E2E_DEBUG: false
```

The tests will:
1. Start PostgreSQL via Testcontainers
2. Run all scenarios against the API
3. Generate reports for artifacts
4. Clean up containers automatically

## Future Enhancements

- **Contract Testing**: Add Pact consumer/provider tests
- **Performance Tests**: Lightweight performance smoke tests
- **OpenAPI Validation**: Validate responses against OpenAPI schema
- **Security Tests**: JWT expiry, RBAC matrix validation
- **Additional Domains**: Reading Rooms, Chat, Sessions, etc.