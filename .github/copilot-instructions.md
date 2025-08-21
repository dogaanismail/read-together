# Read Together - Stutter Support Circle
Read Together is a monorepo containing a Spring Boot backend and React frontend that helps people with speech difficulties practice reading together in supportive virtual rooms. The platform features live reading rooms with audio/video streaming, real-time chat, personal book libraries, progress tracking, social feeds, and gamification elements to encourage consistent practice.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Essential Environment Setup
Set Java 21 environment variables first - CRITICAL for backend operations:
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### Build and Test Commands
- **Backend Build**: `./gradlew :read-together-backend:build --no-daemon` -- takes 3 minutes. NEVER CANCEL. Set timeout to 5+ minutes.
- **Backend Tests**: `./gradlew :read-together-backend:test --no-daemon` -- takes 20 seconds. NEVER CANCEL. Set timeout to 2+ minutes.
- **Frontend Install**: `cd read-together-client && npm install` -- takes 10+ minutes. NEVER CANCEL. Set timeout to 15+ minutes.
- **Frontend Build**: `cd read-together-client && npm run build` -- takes 6 seconds. Set timeout to 30+ seconds.
- **Frontend Lint**: `cd read-together-client && npm run lint` -- takes 3 seconds but may show existing issues.

### Running Applications
**Frontend Development Server:**
```bash
cd read-together-client
npm run dev  # Starts on http://localhost:8080 in 423ms
```

**Frontend Preview Server:**
```bash
cd read-together-client
npm run build && npm run preview  # Serves production build on http://localhost:4173
```

**Backend Server:**
```bash
# REQUIRES PostgreSQL database setup - see Database Requirements below
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
./gradlew :read-together-backend:bootRun --no-daemon  # Runs on port 5006
```

## Database Requirements
The backend requires a PostgreSQL database to run. Without it, the backend will fail with connection errors. 

**Database Configuration:**
- Default connection: `jdbc:postgresql://localhost:5434/read-together-app-db`
- Username: `default` (configurable via `DB_SERVICE_USERNAME`)
- Password: `default` (configurable via `DB_SERVICE_PASSWORD`)

## Validation Scenarios
**ALWAYS manually validate any new code by testing these scenarios:**

1. **Frontend Development Workflow:**
   - Run `npm install` (wait 10+ minutes - be patient)
   - Run `npm run build` (6 seconds)
   - Run `npm run dev` and verify server starts on port 8080
   - Access http://localhost:8080 to see the application UI

2. **Backend Development Workflow:**
   - Set Java 21 environment variables
   - Run `./gradlew :read-together-backend:build --no-daemon` (3 minutes)
   - Run `./gradlew :read-together-backend:test --no-daemon` (20 seconds)
   - Attempt to run backend (will fail without database - this is expected)

3. **Code Quality Checks:**
   - Run `npm run lint` to check frontend code quality
   - Build both frontend and backend to ensure no compilation errors

## Critical Timing and Timeout Guidelines
**NEVER CANCEL THESE OPERATIONS:**
- Backend build: 3 minutes (use 5+ minute timeout)
- Backend tests: 20 seconds (use 2+ minute timeout) 
- Frontend npm install: 10+ minutes (use 15+ minute timeout)
- Frontend build: 6 seconds (use 30+ second timeout)
- Frontend dev server startup: <1 second (use 30+ second timeout)

## Project Structure

### Backend (`read-together-backend/`)
- **Framework**: Spring Boot 3.x with Java 21
- **Build Tool**: Gradle with wrapper
- **Database**: PostgreSQL + Redis
- **Key Features**: JWT authentication, WebSocket, AWS S3 integration
- **Architecture**: Domain-based monolith with modules:
  - `user/` - User management and authentication
  - `security/` - JWT and OAuth configuration
  - `readingroom/` - Reading room management
  - `chat/` - Real-time messaging
  - `session/` - Audio/video session handling
  - `common/` - Shared utilities and models

### Frontend (`read-together-client/`)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 5.x
- **UI Library**: shadcn-ui + Radix UI components
- **Styling**: Tailwind CSS
- **Key Features**: Real-time chat, audio/video streaming, progress tracking

### Configuration Files
- **Backend**: `application.properties`, `application-local.properties`
- **Frontend**: `vite.config.ts`, `tailwind.config.ts`, `eslint.config.js`
- **Root**: `build.gradle` (defines monorepo build tasks)

## Common Issues and Solutions

### Frontend Issues
- **`vite: not found`**: Run `npm install` first - the dependency installation may take 10+ minutes
- **ESLint errors**: The codebase has existing linting issues - focus only on new code you're adding
- **Build warnings**: Large chunk size warnings are expected due to the rich UI component library

### Backend Issues  
- **Database connection failures**: Expected without PostgreSQL setup
- **Java version errors**: Ensure Java 21 is set with `JAVA_HOME` and `PATH`
- **Gradle daemon issues**: Use `--no-daemon` flag for consistency

## Integration and APIs
- **Backend API**: Runs on port 5006 with `/api/v1` base path
- **Frontend Dev**: Runs on port 8080, proxies API calls to backend
- **WebSocket**: Used for real-time chat and reading room features
- **File Upload**: Supports audio/video session recordings up to 500MB

## Development Commands Reference
```bash
# Root level - build both projects
./gradlew buildAll  # Custom task to build backend + frontend

# Backend only
cd read-together-backend
./gradlew build --no-daemon
./gradlew test --no-daemon  
./gradlew bootRun --no-daemon

# Frontend only  
cd read-together-client
npm install  # 10+ minutes first time
npm run build  # 6 seconds
npm run dev  # Development server
npm run lint  # Code quality check
npm run preview  # Preview production build
```

## Always Follow These Rules
- **Set Java 21 environment** before any backend operations
- **Wait for npm install to complete** before frontend operations (10+ minutes)
- **Use appropriate timeouts** for all build commands - don't cancel early
- **Test actual functionality** after making changes - don't just build and stop
- **Check both frontend and backend** when making full-stack changes
- **Run linting** but focus on new code rather than fixing existing issues