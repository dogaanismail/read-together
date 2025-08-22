# Read Together - Backend Coding Standards & Conventions

This document outlines our coding standards, conventions, and architectural patterns for the Read Together backend project. These standards ensure consistency, maintainability, and effective collaboration.

## 🏗️ Architecture Patterns

### Domain-Based Monolith
- Each domain is a self-contained module (User, Session, Feed, etc.)
- Domain structure: `controller/`, `service/`, `repository/`, `entity/`, `model/`, `factory/`
- Clear separation of concerns across layers

### Clean Architecture Principles
- **Controllers**: Only handle HTTP requests/responses, no business logic
- **Services**: Contain all business logic and orchestration
- **Repositories**: Data access layer with JPA queries
- **Entities**: Database entities using JPA annotations
- **Models**: DTOs for request/response objects
- **Factories**: Object construction using factory pattern

## 📝 Code Quality Standards

### 1. No Comments Policy
- **Rule**: Code should be self-documenting
- **Exception**: Only public API documentation (Javadoc) when necessary
- ❌ Avoid: `// Get user sessions` 
- ✅ Prefer: Clear method names like `getUserSessions()`

### 2. Proper Spacing
- **Rule**: Always add empty line between methods
- **Rule**: Add empty line between logical blocks within methods
- **Rule**: Add empty line after method signature for all methods
```java
public void method1() {

    // implementation
}

public void method2() {

    // implementation  
}
```

### 3. Multi-Parameter Method Formatting
- **Rule**: When method has 2+ parameters, use line-by-line formatting
- **Rule**: Each parameter on its own line, aligned with first parameter
- **Rule**: Always add empty line after multi-parameter method signature
```java
// ✅ Correct - Multi-parameter formatting
public static FeedItemEntity createAchievementFeedItem(
        UUID userId, 
        UUID achievementId,
        String title, 
        String description) {

    return FeedItemEntity.builder()
            .userId(userId)
            .itemType(FeedItemEntity.FeedItemType.ACHIEVEMENT)
            // ... other fields
            .build();
}

// ✅ Correct - Single parameter (no line breaks needed)
@PostMapping("/{id}/view")
public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {

    feedService.incrementViewCount(id);
    return ResponseEntity.ok().build();
}
```

### 4. Primitive vs Object Types
- **Rule**: Use primitive types (`boolean`, `long`, `int`) when field has `@Builder.Default`
- **Rule**: Use object types (`Boolean`, `Long`, `Integer`) when field can be null
```java
// ✅ Correct - has default value
@Builder.Default
private boolean isPublic = false;

// ✅ Correct - can be null
private Integer durationSeconds;
```

## 🏷️ Naming Conventions

### Entity Naming
```java
@Entity(name = "session")           // Entity name in JPQL
@Table(name = "sessions")           // Database table name
public class SessionEntity {        // Class name with "Entity" suffix
```

### ID Fields
- **Always use UUID**: `private UUID id;`
- **Generation**: `@GeneratedValue(strategy = GenerationType.UUID)`
- **Foreign Keys**: `private UUID userId;` (not `user_id`)

### Method Naming
- **Controllers**: HTTP action focused (`createSession`, `getUserSessions`)
- **Services**: Business action focused (`validateFileType`, `processFileUpload`)
- **Repositories**: Query focused (`findByUserIdOrderByCreatedAtDesc`)

## 🏭 Factory Pattern

### Entity Creation
- **Rule**: Use factory classes for complex entity construction
- **Pattern**: `@UtilityClass` with static methods
```java
@UtilityClass
public class SessionEntityFactory {
    
    public static SessionEntity createFromRequest(
            UUID userId,
            SessionCreateRequest request,
            MultipartFile file) {
        
        return SessionEntity.builder()
                .userId(userId)
                .title(request.getTitle())
                // ... other fields
                .build();
    }
}
```

## 📊 Lombok Usage

### Entity Annotations (in order)
```java
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "entityName")
@Table(name = "table_name")
public class EntityName extends BaseEntity {
```

### Service/Controller Annotations
```java
@Slf4j
@Service  // or @RestController
@RequiredArgsConstructor
public class ServiceName {
```

### Builder Defaults
```java
@Column(name = "is_active")
@Builder.Default
private boolean isActive = true;  // primitive type since it has default
```

## 🌐 Controller Patterns

### No Business Logic Rule
- **Controllers handle**: HTTP mapping, validation, response formatting
- **Controllers delegate**: All business logic to services
```java
@PostMapping
public ResponseEntity<SessionResponse> createSession(
        @Valid @RequestPart("session") SessionCreateRequest request,
        @RequestPart("file") MultipartFile file,
        Authentication authentication) {
    
    // ✅ Good - delegate to service
    SessionResponse session = sessionService.createSession(request, file, authentication);
    return ResponseEntity.ok(session);
}
```

### Error Handling
- **Pattern**: Try-catch in controller, log error, return appropriate HTTP status
- **Service exceptions**: Let services throw domain-specific exceptions
```java
try {
    SessionResponse session = sessionService.createSession(request, file, authentication);
    return ResponseEntity.ok(session);
} catch (Exception e) {
    log.error("Failed to create session", e);
    return ResponseEntity.badRequest().build();
}
```

## 🔧 Service Patterns

### Authentication Handling
- **Pattern**: Services receive `Authentication` parameter and extract user ID
```java
private UUID getCurrentUserId(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    return UUID.fromString(jwt.getClaimAsString(USER_ID.getValue()));
}
```

### Pagination
- **Pattern**: Controllers pass `int page, int size`, service creates `Pageable`
```java
public Page<SessionResponse> getUserSessions(int page, int size, Authentication authentication) {
    UUID userId = getCurrentUserId(authentication);
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByUserId(userId, pageable).map(this::mapToResponse);
}
```

### Async Operations
- **Pattern**: Use `@Async` annotation and `CompletableFuture<T>`
```java
@Async
public CompletableFuture<String> processFileUploadAsync(UUID sessionId, MultipartFile file) {
    return storageService.uploadFileAsync(file, fileName, folder);
}
```

## 🗃️ Repository Patterns

### Query Method Naming
```java
// Find with ordering
Page<Entity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

// Find with multiple conditions
List<Entity> findByIsPublicTrueAndProcessingStatus(ProcessingStatus status);

// Custom JPQL queries
@Query("SELECT e FROM entityName e WHERE e.field = :value")
List<Entity> customQuery(@Param("value") String value);
```

### Update Queries
```java
@Modifying
@Query("UPDATE entityName e SET e.field = e.field + 1 WHERE e.id = :id")
void incrementField(@Param("id") UUID id);
```

## 📋 Configuration Standards

### Application Properties Structure
```properties
# Feature Configuration
feature.setting=${ENV_VAR:default-value}

# External Service Configuration  
service.provider=${SERVICE_PROVIDER:local}
service.s3.bucket=${AWS_S3_BUCKET:default-bucket}

# Security Configuration
app.jwt.secret=${JWT_SECRET:default-secret}
```

### Configurable Services
- **Pattern**: Use configuration classes to switch implementations
- **Example**: Storage service (S3 vs Local) based on environment

## 🧪 Testing Patterns

### Service Testing
- **Pattern**: Mock dependencies, test business logic
- **Focus**: Verify correct method calls and data transformations

### Repository Testing
- **Pattern**: Use `@DataJpaTest` for repository layer
- **Focus**: Verify query correctness and database interactions

# Backend Coding Standards

## Testing Standards

### Unit Testing Structure

#### Test Organization
- **Fixtures Directory**: `src/test/java/org/readtogether/{domain}/fixtures/`
- **Factory Tests**: `src/test/java/org/readtogether/{domain}/factory/`
- **Service Tests**: `src/test/java/org/readtogether/{domain}/service/`

#### Fixture Naming Conventions
Follow the pattern: `{create}.{generic-name}.{entity-name}`

**Examples:**
```java
// Entity Fixtures
createDefaultUserEntity()
createSecondaryUserEntity() 
createAdminUserEntity()

// Request Fixtures
createDefaultRegisterRequest()
createPrivacyUpdateRequestWithNulls()
createPartialReadingPreferencesRequest()

// Response Fixtures
createDefaultPrivacySettingsResponse()
createFastReaderResponse()
```

#### Fixture Class Structure
```java
@UtilityClass
public class UserEntityFixtures {

    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    public static UserEntity createDefaultUserEntity() {

        return createUserEntity(DEFAULT_USER_ID,
                "test@example.com",
                "John",
                "Doe",
                UserType.USER
        );
    }

    public static UserEntity createUserEntity(
            UUID id,
            String email,
            String firstName,
            String lastName,
            UserType userType) {

        return UserEntity.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .userType(userType)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
```

#### Factory Test Standards
- **Use fixtures instead of inline object construction**
- **Only keep Given/When/Then comments**
- **Reference fixtures from other fixture classes when needed**

**Good Example:**
```java
@Test
@DisplayName("Should create user entity from register request with USER role")
void shouldCreateUserEntityFromRegisterRequestWithUserRole() {
    // Given
    RegisterRequest request = RequestFixtures.createDefaultRegisterRequest();

    // When
    UserEntity result = UserEntityFactory.getUserEntityByRegisterRequest(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("newuser@example.com");
    assertThat(result.getUserType()).isEqualTo(UserType.USER);
}
```

**Avoid:**
```java
// Don't create objects inline in tests
RegisterRequest request = RegisterRequest.builder()
    .email("test@example.com")
    .firstName("Test")
    .lastName("User")
    .role("user")
    .build();
```

#### Service Test Standards
- **Use Mockito for dependencies**
- **Test happy paths, edge cases, and exceptions**
- **Use fixtures for test data**
- **Verify mock interactions**

#### Test Method Formatting
- **Empty line after method signature**
- **Use `var` for obvious types in Given section**
- **Group assertions logically**

```java
public static UserEntity createDefaultUserEntity() {

    return createUserEntity(DEFAULT_USER_ID,
            "test@example.com", 
            "John",
            "Doe",
            UserType.USER
    );
}
```

### Key Principles
1. **DRY (Don't Repeat Yourself)**: Use fixtures to eliminate duplication
2. **Maintainability**: Centralize test data in fixture classes
3. **Readability**: Clear naming conventions and minimal comments
4. **Consistency**: Follow established patterns across all domains

---

## General Coding Standards

### Package Structure
```
src/main/java/org/readtogether/
├── common/           # Shared utilities and base classes
├── config/           # Configuration classes
├── security/         # Authentication and authorization
└── {domain}/         # Domain-specific packages
    ├── controller/   # REST controllers
    ├── service/      # Business logic
    ├── repository/   # Data access
    ├── entity/       # JPA entities
    ├── model/        # DTOs and domain models
    │   ├── request/  # Request DTOs
    │   └── response/ # Response DTOs
    ├── factory/      # Object creation utilities
    ├── mapper/       # Entity-DTO mappers
    ├── exception/    # Domain-specific exceptions
    └── utils/        # Domain utilities
```

### Naming Conventions
- **Classes**: PascalCase (`UserService`, `ReadingRoomController`)
- **Methods**: camelCase (`getCurrentUser`, `createReadingRoom`)
- **Constants**: UPPER_SNAKE_CASE (`DEFAULT_ROOM_SIZE`, `MAX_PARTICIPANTS`)
- **Packages**: lowercase (`user`, `readingroom`, `notification`)

### Code Style
- **Indentation**: 4 spaces
- **Line Length**: 120 characters maximum
- **Imports**: Group by package, static imports last
- **Method Length**: Keep methods under 20 lines when possible

### Documentation
- **Public APIs**: Include Javadoc with @param and @return
- **Complex Logic**: Add inline comments explaining the "why"
- **README**: Each module should have usage examples

### Exception Handling
- **Custom Exceptions**: Create domain-specific exceptions
- **Global Handler**: Use @ControllerAdvice for centralized error handling
- **Logging**: Log exceptions with appropriate levels

### Database
- **Entity Naming**: Use singular nouns (`User`, `ReadingRoom`)
- **Table Naming**: Use snake_case (`user`, `reading_room`)
- **Foreign Keys**: Follow pattern `{table}_id` (`user_id`, `room_id`)
- **Indexes**: Add indexes for frequently queried columns

### Security
- **Authentication**: JWT-based with proper validation
- **Authorization**: Method-level security annotations
- **Input Validation**: Use Bean Validation annotations
- **SQL Injection**: Always use parameterized queries
