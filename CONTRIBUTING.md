# Contributing to Read Together

Thanks for your interest in improving Read Together. Contributions are welcome and appreciated.

## Development setup
- Java 21 (set JAVA_HOME)
- Node.js 18+ and npm
- Docker (for local PostgreSQL)

### Start services
```bash
# Database
cd docker
docker compose up -d
```

### Backend
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew :read-together-backend:build --no-daemon
./gradlew :read-together-backend:test --no-daemon
./gradlew :read-together-backend:bootRun --no-daemon
```

### Frontend
```bash
cd read-together-client
npm install
npm run dev
```

## Coding guidelines
- Backend: Spring Boot 3, Java 21. Keep modules cohesive (user, security, readingroom, chat, session, common). Prefer explicit validations and clear DTOs.
- Frontend: React + TS. Favor functional components, hooks, and accessible UI. Tailwind and shadcn-ui for styling.
- Write minimal tests for new behavior when feasible. Keep PRs focused and small.

## Commit/PR process
- Use descriptive commit messages.
- Link issues when relevant and describe the change, rationale, and testing steps.
- Ensure builds pass locally before opening a PR.

## Security
If you find a security vulnerability, please open a private discussion or email the maintainer (see GitHub profile) rather than filing a public issue first.
# Read Together — Stutter Support Circle

A compassionate, open-source platform that helps people who stutter practice reading together in supportive virtual rooms. Read Together offers live reading rooms with audio/video, real‑time chat, personal libraries, progress tracking, and gentle gamification to encourage consistent practice.

- Frontend: React 18 + TypeScript (Vite, Tailwind, shadcn-ui)
- Backend: Spring Boot 3 (Java 21), PostgreSQL, Liquibase, WebSocket, OpenAPI
- Infra: Docker (PostgreSQL dev), AWS S3 optional for media storage


## Why
Stuttering can make reading aloud stressful. Practicing in a safe, encouraging space—with peers who understand—builds confidence and fluency over time. Read Together is built for accessibility, psychological safety, and community.


## Features
- Supportive live reading rooms (audio/video) with privacy controls
- Real-time chat and emoji reactions
- Personal book library and reading goals
- Progress tracking and activity graphs
- Social feed and achievement badges
- Large file uploads (session audio/video) up to 500MB

## Monorepo layout
- read-together-backend/ — Spring Boot app (port 5006)
- read-together-client/ — React app (dev server port 8080)
- docker/ — local development Docker Compose (PostgreSQL)
- read-together-db-migration-changelog/ — Liquibase changelog module


## Ports and defaults (verified from source)
- Frontend dev: http://localhost:8080 (vite.config.ts)
- Backend API: http://localhost:5006 (application.properties)
- API base path: /api/v1
- Database default URL: jdbc:postgresql://localhost:5433/read-together-app-db (application.properties)
- Docker Compose DB: postgres exposed on 5433 (docker/docker-compose.yaml)
- File uploads: max 500MB (application.properties)

Note: Some older docs reference DB port 5434. The current code and docker-compose use 5433 by default.


## Quick start (local)
Prerequisites:
- Java 21 (ensure JAVA_HOME is set)
- Node.js 18+ and npm
- Docker (optional but recommended for local PostgreSQL)

1) Start PostgreSQL locally (Docker)

```bash
cd docker
docker compose up -d
```

This starts Postgres on localhost:5433 with db/read-together-app-db and user/pass default/default.

2) Backend – build, test, run

```bash
# set Java 21 (adapt path for your OS)
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# from repo root
./gradlew :read-together-backend:build --no-daemon
./gradlew :read-together-backend:test --no-daemon

# run (requires the DB above)
./gradlew :read-together-backend:bootRun --no-daemon
```

Key env vars (all optional; sensible defaults provided):
- DB_SERVICE_URL (default jdbc:postgresql://localhost:5433/read-together-app-db)
- DB_SERVICE_USERNAME (default default)
- DB_SERVICE_PASSWORD (default default)
- STORAGE_PROVIDER (local | s3) – default local
- LOCAL_STORAGE_PATH (default /tmp/read-together/uploads)

3) Frontend – install and run

```bash
cd read-together-client
npm install
npm run dev
```

Open http://localhost:8080. The app proxies API calls to the backend port 5006.

Combined build from root:

```bash
./gradlew buildAll
```


## Technology highlights
- Spring Boot 3 with Web, Security, OAuth2 Resource Server, JPA, WebSocket
- OpenAPI via springdoc and Feign clients where needed
- Liquibase migrations and PostgreSQL
- Async processing and configurable storage providers (local/S3)
- React + Vite + TypeScript with shadcn-ui and Tailwind CSS
- State/data: React Query; charts: Recharts
- Mobile: Capacitor (Android/iOS)


## Contributing
We welcome contributions focused on accessibility, performance, and user comfort. See CONTRIBUTING.md for setup, coding standards, and tips. Issues and PRs are appreciated.


## Sponsorship
If this project resonates with you, please consider sponsoring to support accessibility and speech therapy tooling in open source.

- GitHub Sponsors: https://github.com/sponsors/dogaanismail

Your support helps keep the project sustainable and community-driven.


## License
This project is licensed under the MIT License. See LICENSE for details.

