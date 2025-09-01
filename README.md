# Read Together — Stutter Support Circle

[![Sponsor](https://img.shields.io/badge/Sponsor-❤-ea4aaa)](https://github.com/sponsors/dogaanismail)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

The journey to confident speech is often a personal one, but it doesn't have to be a lonely one. Stuttering can be an isolating experience, and finding a safe, non-judgmental space to practice and track progress is a significant challenge for many.

Read Together is a web platform dedicated to empowering People Who Stutter (PWS) by providing modern tools and a supportive community for speech practice. Our mission is to transform the solitary act of practice into a shared, encouraging, and empowering experience.


## Core Features
This platform helps users build confidence by focusing on three key areas:

- 📈 Practice & Track Progress: Users can record video or audio sessions of themselves reading aloud or speaking freely. These sessions are saved chronologically, creating a personal and visual log that makes it easy to see and appreciate progress over time.
- 🔒 Share Securely & Confidently: Every user has full control over their privacy. Sessions can be kept completely private for personal review or shared with the community to receive encouragement, exchange feedback, and celebrate milestones.
- 🤝 Build a Supportive Network: The platform is more than a tool; it's a community. Through features like comments, likes, and live group practice rooms, users can connect with peers from around the world who understand the journey, fostering a global network of support.

Additional highlights:
- Live reading rooms (audio/video) with real-time chat and emoji reactions
- Personal book library, goals, achievements, and progress graphs
- Large file uploads (audio/video) up to 500MB with local or S3 storage

## Monorepo at a glance
- read-together-backend/ — Spring Boot app (port 5006)
- read-together-client/ — React + Vite app (dev server port 8080)
- read-together-db-migration-changelog/ — Liquibase changelog module
- docker/ — Local PostgreSQL via Docker Compose


## Tech stack
- Backend: Spring Boot 3 (Java 21), JPA, Liquibase, WebSocket, OpenAPI, Feign
- Database: PostgreSQL (dev via Docker), optional AWS S3 for media
- Frontend: React 18 + TypeScript, Vite, Tailwind CSS, shadcn-ui, Radix
- State/data: React Query; charts: Recharts; mobile with Capacitor


## Ports and configuration (from source)
- Frontend dev: http://localhost:8080 (vite.config.ts)
- Backend API: http://localhost:5006 (application.properties)
- API base path: /api/v1
- Database: jdbc:postgresql://localhost:5433/read-together-app-db (application.properties)
- Docker Compose DB: Postgres exposed on 5433 (docker/docker-compose.yaml)
- File uploads: up to 500MB

Note: If you see older docs mentioning port 5434 for PostgreSQL, the current configuration uses 5433 by default.


## Quick start (local development)

Prerequisites:
- Java 21 (set JAVA_HOME)
- Node.js 18+ and npm
- Docker (optional but recommended for local PostgreSQL)

1) Database via Docker

```bash
cd docker
docker compose up -d
```

This starts PostgreSQL on localhost:5433 with database read-together-app-db and user/password default/default.

2) Backend — build, test, run

```bash
# set Java 21 (example path; adjust for your OS)
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# from repo root
./gradlew :read-together-backend:build --no-daemon
./gradlew :read-together-backend:test --no-daemon
./gradlew :read-together-backend:bootRun --no-daemon
```

Optional environment variables (defaults in application.properties):
- DB_SERVICE_URL (default jdbc:postgresql://localhost:5433/read-together-app-db)
- DB_SERVICE_USERNAME (default default)
- DB_SERVICE_PASSWORD (default default)
- STORAGE_PROVIDER (local | s3) — default local
- LOCAL_STORAGE_PATH (default /tmp/read-together/uploads)
- AWS_S3_BUCKET, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_S3_ENDPOINT (if using S3)

3) Frontend — install and run

```bash
cd read-together-client
npm install
npm run dev
```

Open http://localhost:8080 to view the app.

4) Build both

```bash
./gradlew buildAll
```


## Contributing
We welcome contributions that improve accessibility, performance, and the overall experience for people who stutter. Please see CONTRIBUTING.md to get started.


## Sponsorship
If this project resonates with you, please consider sponsoring to support accessible, community-driven tools for speech practice.

- GitHub Sponsors: https://github.com/sponsors/dogaanismail

Your support helps keep Read Together sustainable and growing.


## License
This project is licensed under the MIT License. See LICENSE for details.

