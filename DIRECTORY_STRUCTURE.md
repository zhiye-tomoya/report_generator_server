# Directory Structure

```
reportGenerator/
├── build.gradle.kts                    # Gradle build configuration
├── settings.gradle.kts
├── gradlew                              # Gradle Wrapper (Unix/Mac)
├── gradlew.bat                          # Gradle Wrapper (Windows)
│
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/
    │   │       └── example/
    │   │           └── reportGenerator/
    │   │               ├── ReportGeneratorApplication.kt      # Main application
    │   │               │
    │   │               ├── config/                            # Configuration classes
    │   │               │   └── SecurityConfig.kt              # Spring Security configuration
    │   │               │
    │   │               ├── controller/                        # Controller layer
    │   │               │   └── AuthController.kt              # Authentication API endpoints
    │   │               │
    │   │               ├── dto/                               # Data Transfer Objects
    │   │               │   ├── AuthResponse.kt                # Authentication response
    │   │               │   ├── LoginRequest.kt                # Login request
    │   │               │   ├── RegisterRequest.kt             # Registration request
    │   │               │   └── UserResponse.kt                # User response
    │   │               │
    │   │               ├── entity/                            # Entity layer
    │   │               │   └── User.kt                        # User entity
    │   │               │
    │   │               ├── exception/                         # Exception handling
    │   │               │   ├── GlobalExceptionHandler.kt      # Global exception handler
    │   │               │   ├── ResourceAlreadyExistsException.kt
    │   │               │   └── ResourceNotFoundException.kt
    │   │               │
    │   │               ├── repository/                        # Repository layer
    │   │               │   └── UserRepository.kt              # User repository
    │   │               │
    │   │               ├── security/                          # Security related
    │   │               │   ├── CustomUserDetailsService.kt    # Custom UserDetailsService
    │   │               │   ├── JwtAuthenticationEntryPoint.kt # JWT authentication entry point
    │   │               │   ├── JwtAuthenticationFilter.kt     # JWT filter
    │   │               │   └── JwtTokenProvider.kt            # JWT token generation & verification
    │   │               │
    │   │               └── service/                           # Service layer
    │   │                   └── AuthService.kt                 # Authentication service
    │   │
    │   └── resources/
    │       ├── application.yml                                # Application configuration
    │       ├── db/
    │       │   └── migration/
    │       │       └── V1__create_users_table.sql             # DB migration
    │       ├── static/
    │       └── templates/
    │
    └── test/
        └── kotlin/
            └── com/
                └── example/
                    └── reportGenerator/
                        └── ReportGeneratorApplicationTests.kt
```

## Layer Architecture

### 1. Controller Layer

- Receives HTTP requests and returns responses
- Performs validation
- File: `controller/AuthController.kt`

### 2. Service Layer

- Implements business logic
- Transaction management
- File: `service/AuthService.kt`

### 3. Repository Layer

- Database access
- Uses Spring Data JPA
- File: `repository/UserRepository.kt`

### 4. Entity Layer

- Maps to database tables
- File: `entity/User.kt`

### 5. DTO Layer

- Data transfer objects
- For requests/responses
- Files: Under `dto/` directory

### 6. Security Layer

- JWT authentication and authorization
- Spring Security configuration
- Files: Under `security/` directory, `config/SecurityConfig.kt`

### 7. Exception Handling

- Global exception handler
- Custom exception classes
- Files: Under `exception/` directory
