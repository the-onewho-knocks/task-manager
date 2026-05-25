# Task Manager API

A RESTful task management API built with Spring Boot 3, secured with JWT authentication and Redis-backed token invalidation. Exposes a fully documented OpenAPI interface via Swagger UI.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Authentication Flow](#authentication-flow)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)

---

## Overview

Task Manager API provides a secure backend for managing tasks through a stateless REST interface. Users register and authenticate via JWT tokens. Logout is enforced by storing invalidated tokens in a Redis blacklist, preventing reuse before expiry. All task operations require a valid Bearer token.

The API supports creating, reading, updating, and deleting tasks, with optional filtering by status (`TODO`, `IN_PROGRESS`, `DONE`).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA, PostgreSQL |
| Security | Spring Security, JWT (jjwt 0.11.5) |
| Token Blacklist | Redis (Spring Data Redis) |
| Validation | Jakarta Bean Validation |
| Documentation | Springdoc OpenAPI 2.3 / Swagger UI |
| Build | Maven |
| Utilities | Lombok |

---

## Architecture

The application follows a standard layered architecture:

```
Controller  ->  Service (interface + impl)  ->  Repository  ->  Database
                     |
               JwtUtil / Redis (auth concerns)
```

Security is handled by a custom `OncePerRequestFilter` (`JwtAuthFilter`) that intercepts every request, checks the Redis blacklist, validates the JWT, and populates the `SecurityContext`. The session policy is fully stateless.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Redis 6+

### 1. Clone the repository

```bash
git clone https://github.com/your-username/task-manager.git
cd task-manager
```

### 2. Create the database

```sql
CREATE DATABASE taskdb;
```

### 3. Configure the application

Copy `src/main/resources/application.properties` and update the values for your environment (see [Configuration](#configuration)).

### 4. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

### 5. Open Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Use the **Authorize** button to enter your Bearer token before calling protected endpoints.

---

## Configuration

All configuration lives in `src/main/resources/application.properties`.

```properties
# Server
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=

# JWT
app.jwt.secret=YOUR_BASE64_ENCODED_SECRET
app.jwt.expiration=86400000
```

**Important:** Replace `app.jwt.secret` with a cryptographically strong Base64-encoded key before deploying. Never commit real secrets to version control.

---

## API Reference

### Authentication

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive a JWT | No |
| POST | `/api/auth/logout` | Invalidate the current token | Yes |

### Tasks

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/tasks` | Create a task | Yes |
| GET | `/api/tasks` | List all tasks (optional `?status=` filter) | Yes |
| GET | `/api/tasks/{id}` | Get a task by ID | Yes |
| PUT | `/api/tasks/{id}` | Update a task | Yes |
| DELETE | `/api/tasks/{id}` | Delete a task | Yes |

#### Task status values

`TODO` `IN_PROGRESS` `DONE`

#### Example: Register

```json
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

#### Example: Create task

```http
POST /api/tasks
Authorization: Bearer <token>

{
  "title": "Write unit tests",
  "description": "Cover the service layer",
  "status": "TODO"
}
```

---

## Authentication Flow

1. Client calls `POST /api/auth/register` or `POST /api/auth/login` and receives a signed JWT.
2. Client includes the token in the `Authorization: Bearer <token>` header on subsequent requests.
3. `JwtAuthFilter` intercepts every request:
   - Checks the Redis blacklist for the token key `blacklist:<token>`.
   - Validates the JWT signature and expiry.
   - Loads the user details and sets the `SecurityContext`.
4. On `POST /api/auth/logout`, the token is written to Redis with a TTL equal to its remaining validity period, effectively blacklisting it until natural expiry.

---

## Project Structure

```
src/main/java/com/taskmanager/
├── config/
│   ├── RedisConfig.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   └── TaskController.java
├── dto/
│   ├── AuthResponseDTO.java
│   ├── LoginRequestDTO.java
│   ├── RegisterRequestDTO.java
│   ├── TaskRequestDTO.java
│   └── TaskResponseDTO.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── Role.java
│   ├── Task.java
│   ├── TaskStatus.java
│   └── User.java
├── repository/
│   ├── TaskRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthFilter.java
│   └── JwtUtil.java
└── service/
    ├── AuthService.java
    ├── TaskService.java
    └── impl/
        ├── AuthServiceImpl.java
        └── TaskServiceImpl.java
```

---

## Running Tests

```bash
mvn test
```

The test suite uses `spring-boot-starter-test` and `spring-security-test`. A context load test is included as a baseline; extend `TaskManagerApplicationTests` with unit and integration tests as the project grows.

---

## License

This project is released under the [MIT License](LICENSE).
