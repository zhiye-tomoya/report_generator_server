# Swagger UI / OpenAPI Documentation Guide

## Overview

This project uses SpringDoc OpenAPI to automatically generate REST API documentation.
With Swagger UI, you can view API specifications and test requests directly from your browser.

## Access URLs

After starting the application, access the following URLs:

### Swagger UI (Interactive API Documentation)

```
http://localhost:8080/swagger-ui.html
```

or

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON Definition

```
http://localhost:8080/v3/api-docs
```

## How to Use Swagger UI

### 1. View API List

When you open Swagger UI, all API endpoints are displayed grouped by controller.

- **Auth Controller**: Authentication-related APIs (registration, login)

### 2. Execute APIs Without Authentication

#### 2.1 User Registration

1. Expand `POST /api/auth/register`
2. Click the "Try it out" button
3. Enter the request body:

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

4. Click the "Execute" button
5. Check the response

#### 2.2 Login

1. Expand `POST /api/auth/login`
2. Click the "Try it out" button
3. Enter the request body:

```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

4. Click the "Execute" button
5. Copy the `token` from the response

### 3. Execute APIs With JWT Authentication

Most API endpoints require JWT token authentication.

#### 3.1 Setting Up JWT Token

1. Click the **"Authorize"** button in the top right of Swagger UI
2. The `bearerAuth` dialog will open
3. Paste the JWT token obtained from login
   - Note: Enter only the token (no `Bearer` prefix required)
   - Example: `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0...`
4. Click the "Authorize" button
5. Close the dialog

#### 3.2 Execute Authenticated APIs

Once the JWT token is set, all subsequent API requests will automatically include the `Authorization: Bearer {token}` header.

### 4. View Response

When you execute an API, the following information is displayed:

- **HTTP Status Code**: 200, 201, 400, 401, 404, etc.
- **Response Body**: Data in JSON format
- **Response Headers**: Content-Type, etc.
- **Execution Time**: Request duration

## Configuration Files

### OpenApiConfig.kt

```kotlin
src/main/kotlin/com/example/reportGenerator/config/OpenApiConfig.kt
```

This file configures:

- API title and description
- Version information
- JWT authentication schema (Bearer token)

### application-dev.yml

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: alpha # Sort APIs alphabetically
    tags-sorter: alpha # Sort tags alphabetically
    display-request-duration: true # Display request duration
    doc-expansion: none # Collapse all by default
```

### SecurityConfig.kt

Swagger UI and OpenAPI endpoints are configured to be accessible without authentication:

```kotlin
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
```

## Customizing API Documentation

### Adding Annotations to Controllers

You can generate more detailed documentation by adding OpenAPI annotations to controllers and endpoints:

```kotlin
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication related APIs")
class AuthController {

    @Operation(
        summary = "User Registration",
        description = "Create a new user account"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Registration successful"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "409", description = "User already exists")
    ])
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        // ...
    }

    @Operation(
        summary = "Login",
        description = "Login with email address and password"
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        // ...
    }
}
```

## Troubleshooting

### Cannot Access Swagger UI

1. Verify the application is running
2. Check the URL is correct (`http://localhost:8080/swagger-ui.html`)
3. If the port number is not 8080, change the URL accordingly

### 401 Unauthorized Error

1. Verify the JWT token is correctly set
2. Check if the token has expired (default: 24 hours)
3. Re-set the token using the "Authorize" button

### APIs Not Displayed

1. Verify the controller has the `@RestController` annotation
2. Check that the path is correctly set with `@RequestMapping`
3. Restart the application

## Production Environment Considerations

It's recommended to disable Swagger UI or restrict access in production:

### application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false # Disable Swagger UI
  api-docs:
    enabled: false # Disable API documentation
```

Or require authentication in SecurityConfig:

```kotlin
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
```

## Reference Links

- [SpringDoc OpenAPI Official Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
