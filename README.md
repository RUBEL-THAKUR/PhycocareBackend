# PsychoCare Backend

Spring Boot 3.3 REST API for the PsychoCare mental health platform.

## Tech Stack

- Java 21
- Spring Boot 3.3
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 8.0
- WebSocket (STOMP over SockJS)
- Lombok
- SpringDoc OpenAPI (Swagger)

## Prerequisites

- Java 21+
- MySQL 8.0+
- Gradle 8+

## Setup

### 1. Create Database

```sql
CREATE DATABASE psychocare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Or run the full schema + seed file:

```bash
mysql -u root -p < database.sql
```

### 2. Configure Environment

Copy `.env.example` to `.env` and fill in your values, or set environment variables directly.

Alternatively, edit `src/main/resources/application.properties`.

### 3. Build and Run

```bash
./gradlew bootRun
```

Or build a JAR:

```bash
./gradlew build
java -jar build/libs/psychocare-backend-1.0.0.jar
```

Server starts on `http://localhost:8081`

## API Documentation

Swagger UI: `http://localhost:8081/swagger-ui.html`

## Default Credentials

| Role      | Email                        | Password    |
|-----------|------------------------------|-------------|
| Admin     | admin@psychocare.com         | Admin@1234  |
| User      | user@psychocare.com          | User@1234   |
| Therapist | sakshi.kochhar@psychocare.com| Admin@1234  |

## API Base URLs

| Module              | Base Path                       |
|---------------------|---------------------------------|
| User Auth           | /api/auth                       |
| User Actions        | /api/user                       |
| Therapist Auth      | /api/therapist                  |
| Therapist Profile   | /api/therapist/profile          |
| Public Therapists   | /api/therapists/public          |
| Admin               | /api/admin                      |
| Chat History        | /api/chat                       |
| WebSocket           | ws://localhost:8081/ws          |

## WebSocket Chat

Connect to `ws://localhost:8081/ws` using SockJS + STOMP.

Subscribe to: `/topic/chat/{sessionId}`

Send to: `/app/chat/{sessionId}`

Payload:
```json
{
  "senderId": "user_123",
  "senderRole": "USER",
  "content": "Hello",
  "messageType": "TEXT"
}
```
# PhycocareBackend
