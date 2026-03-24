# JWT Authentication - Implementation Documentation

## Overview

JWT-based authentication functionality has been implemented using Spring Boot.

## Implementation Details

### Authentication Features

- ✅ JWT-based authentication
- ✅ Token generation and verification
- ✅ Password BCrypt hashing
- ✅ Authorization control via Spring Security
- ✅ Refresh token support

### API Endpoints

#### 1. User Registration

```
POST /api/auth/register
Content-Type: application/json

{
  "name": "Taro Yamada",
  "email": "yamada@example.com",
  "password": "password123"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "name": "Taro Yamada",
  "email": "yamada@example.com",
  "createdAt": "2026-03-19T05:45:00"
}
```

#### 2. Login

```
POST /api/auth/login
Content-Type: application/json

{
  "email": "yamada@example.com",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

**Note:** The refresh token is automatically set as an HttpOnly cookie and is not included in the response body for security reasons.

**Cookies Set:**

- `refreshToken`: HttpOnly, Secure, Path=/api/auth, Max-Age=7 days

#### 3. Get User Information (Authentication Required)

```
GET /api/auth/me
Authorization: Bearer <token>
```

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Taro Yamada",
  "email": "yamada@example.com",
  "createdAt": "2026-03-19T05:45:00"
}
```

#### 4. Refresh Token

```
POST /api/auth/refresh
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

**Note:** This endpoint reads the refresh token from the HttpOnly cookie set during login. A new access token and refresh token are generated and returned. The new refresh token is set as an HttpOnly cookie.

**Cookies Set:**

- `refreshToken`: HttpOnly, Secure, Path=/api/auth, Max-Age=7 days (updated with new token)

## Security Configuration

### Paths That Don't Require Authentication

- `/api/auth/register` - User registration
- `/api/auth/login` - Login
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI documentation

### Paths That Require Authentication

- All endpoints except those listed above

## Validation

### RegisterRequest

- `name`: Required, 2-100 characters
- `email`: Required, valid email format
- `password`: Required, 6-100 characters

### LoginRequest

- `email`: Required, valid email format
- `password`: Required

## JWT Configuration (application.yml)

```yaml
jwt:
  secret: your-secret-key-change-this-in-production-minimum-256-bits-for-hs256-algorithm
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days
```

⚠️ **Important**: Make sure to change `jwt.secret` in production!

## Database Configuration

### MySQL Configuration

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/report_generator?createDatabaseIfNotExist=true
    username: root
    password: password
```

### Flyway Migration

- `V1__create_users_table.sql`: Creates users table

## How to Start

### 1. Database Setup

Start MySQL and verify that the database is automatically created.

```bash
# Start MySQL (using Docker)
docker run -d \
  --name mysql-report-generator \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=report_generator \
  -p 3306:3306 \
  mysql:8.0
```

### 2. Start Application

```bash
# Build and run with Gradle
./gradlew bootRun

# Or
./gradlew build
java -jar build/libs/reportGenerator-0.0.1-SNAPSHOT.jar
```

### 3. Test Functionality

```bash
# User registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Get user information (set token)
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <obtained_token>"
```

## Error Handling

### Validation Error (400 Bad Request)

```json
{
  "timestamp": "2026-03-19T05:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Email should be valid",
    "password": "Password must be between 6 and 100 characters"
  }
}
```

### Authentication Error (401 Unauthorized)

```json
{
  "timestamp": "2026-03-19T05:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

### Email Duplication Error (409 Conflict)

```json
{
  "timestamp": "2026-03-19T05:45:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: test@example.com",
  "path": "/api/auth/register"
}
```

## Main Classes

### Security Related

- `SecurityConfig`: Spring Security configuration
- `JwtTokenProvider`: JWT token generation and verification
- `JwtAuthenticationFilter`: Extracts JWT from requests and authenticates
- `JwtAuthenticationEntryPoint`: Handles authentication errors
- `CustomUserDetailsService`: Loads user information

### Business Logic

- `AuthService`: Authentication-related business logic
- `AuthController`: Authentication API endpoints
- `GlobalExceptionHandler`: Unified exception handling

### Data Access

- `User`: User entity
- `UserRepository`: User CRUD operations

## Technology Stack

- **Language**: Kotlin 2.2.21
- **Framework**: Spring Boot 4.0.3
- **Security**: Spring Security + JWT (jjwt 0.12.5)
- **Database**: MySQL 8.0
- **Migration**: Flyway
- **Build Tool**: Gradle (Kotlin DSL)

## Future Enhancement Ideas

- [ ] Email address verification
- [ ] Password reset functionality
- [ ] User role and permission management
- [ ] OAuth2 support
- [ ] Two-factor authentication
- [ ] Account lockout functionality
