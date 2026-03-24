package com.example.reportGenerator.controller

import com.example.reportGenerator.dto.*
import com.example.reportGenerator.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        logger.info("POST /api/auth/register - Register request received")
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        logger.info("POST /api/auth/login - Login request received")
        val authResult = authService.login(request)
        
        // Set refresh token as HttpOnly cookie
        val refreshTokenCookie = Cookie("refreshToken", authResult.refreshToken).apply {
            isHttpOnly = true
            secure = true // HTTPS only
            path = "/api/auth"
            maxAge = 7 * 24 * 60 * 60 // 7 days in seconds
        }
        response.addCookie(refreshTokenCookie)
        
        return ResponseEntity.ok(authResult.authResponse)
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        logger.info("GET /api/auth/me - Get current user request received")
        val response = authService.getCurrentUser()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        logger.info("POST /api/auth/refresh - Refresh token request received")
        
        // Get refresh token from cookie
        val refreshTokenCookie = request.cookies?.find { it.name == "refreshToken" }
            ?: throw RuntimeException("Refresh token not found")
        
        val authResult = authService.refreshToken(refreshTokenCookie.value)
        
        // Set new refresh token as HttpOnly cookie
        val newRefreshTokenCookie = Cookie("refreshToken", authResult.refreshToken).apply {
            isHttpOnly = true
            secure = true // HTTPS only
            path = "/api/auth"
            maxAge = 7 * 24 * 60 * 60 // 7 days in seconds
        }
        response.addCookie(newRefreshTokenCookie)
        
        return ResponseEntity.ok(authResult.authResponse)
    }
}
