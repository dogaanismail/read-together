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

## 📚 Dependencies & Libraries

### Required Dependencies
- **Lombok**: `@SuperBuilder`, `@RequiredArgsConstructor`, `@Slf4j`
- **Validation**: `@Valid`, `@NotNull`, `@NotBlank`
- **JPA**: `@Entity`, `@Repository`, `@Transactional`
- **Security**: JWT token handling with Spring Security
- **AWS**: S3 integration for file storage

### Code Organization
```
domain/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # JPA entities
├── model/          # DTOs (Request/Response)
├── factory/        # Entity factories
└── exception/      # Domain exceptions
```

## 🔄 Refactoring Checklist

When adding new features or refactoring existing code:

- [ ] ✅ No comments in code (self-documenting)
- [ ] ✅ Proper spacing between methods
- [ ] ✅ Primitive types for `@Builder.Default` fields
- [ ] ✅ UUID for all entity IDs
- [ ] ✅ Controllers delegate to services (no business logic)
- [ ] ✅ Factory pattern for entity creation
- [ ] ✅ Proper Lombok annotations in correct order
- [ ] ✅ Authentication handling in services
- [ ] ✅ Consistent naming conventions
- [ ] ✅ Async operations where appropriate

## 🤝 Collaboration Guidelines

### Code Review Focus Areas
1. **Architecture**: Does it follow our domain patterns?
2. **Business Logic**: Is it in the right layer (service, not controller)?
3. **Data Types**: Correct primitive vs object type usage?
4. **Naming**: Consistent with our conventions?
5. **Testing**: Adequate coverage of business logic?

### Communication
- **Issues**: Reference this document when discussing code standards
- **Improvements**: Update this document when patterns evolve
- **New Features**: Follow established patterns from existing domains

---

*This document should be updated as our coding standards evolve. All team members should refer to this guide to maintain consistency across the codebase.*

## 🔧 Utility Classes

### When to Create Utility Classes
- **Rule**: Always check if a method can be moved to a utility class
- **Criteria**: Pure functions with no dependencies on instance state
- **Pattern**: Methods that could be reused across multiple domains
- **Location**: Place in `org.readtogether.common.util` package

### Utility Class Guidelines
```java
// ✅ Good candidates for utility classes
private String determineContentType(String filename) { ... }
private String formatFileSize(Long bytes) { ... }
private String formatTimeAgo(Instant dateTime) { ... }
private boolean isValidEmail(String email) { ... }

// ❌ Should NOT be in utility classes
private User getCurrentUser() { ... }  // Uses authentication context
private void saveEntity(Entity entity) { ... }  // Uses repository dependency
```

### Utility Class Structure
```java
@UtilityClass
public class FileUtils {
    
    public static String determineContentType(String filename) {
        
        if (filename == null || !filename.contains(".")) {
            return "application/octet-stream";
        }
        
        String extension = extractFileExtension(filename);
        return CONTENT_TYPE_MAP.getOrDefault(extension, "application/octet-stream");
    }
    
    public static boolean isImageFile(String filename) {
        
        String extension = extractFileExtension(filename);
        return extension.equals("jpg") || extension.equals("jpeg");
    }
}
```

### Common Utility Classes
- **FileUtils**: File operations, content type detection, size formatting
- **TimeUtils**: Date/time formatting, relative time calculations
- **EngagementUtils**: Social media metrics formatting (likes, views, etc.)
- **ValidationUtils**: Input validation, format checking
- **StringUtils**: String manipulation, formatting operations
