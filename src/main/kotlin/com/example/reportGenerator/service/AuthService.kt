package com.example.reportGenerator.service

import com.example.reportGenerator.dto.*
import com.example.reportGenerator.entity.User
import com.example.reportGenerator.exception.ResourceAlreadyExistsException
import com.example.reportGenerator.repository.UserRepository
import com.example.reportGenerator.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun register(request: RegisterRequest): UserResponse {
        val normalizedEmail = request.email.lowercase()
        logger.info("Registering new user with email: $normalizedEmail")

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw ResourceAlreadyExistsException("Email already exists: $normalizedEmail")
        }

        val user = User(
            name = request.name,
            email = normalizedEmail,
            password = passwordEncoder.encode(request.password) ?: ""
        )

        val savedUser = userRepository.save(user)
        logger.info("User registered successfully with id: ${savedUser.id}")

        return UserResponse(
            id = savedUser.id!!,
            name = savedUser.name,
            email = savedUser.email,
            createdAt = savedUser.createdAt!!
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val normalizedEmail = request.email.lowercase()
        logger.info("User login attempt for email: $normalizedEmail")

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(normalizedEmail, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = jwtTokenProvider.generateToken(authentication)
        val refreshToken = jwtTokenProvider.generateRefreshToken(normalizedEmail)

        logger.info("User logged in successfully: $normalizedEmail")

        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            expiresIn = jwtTokenProvider.getExpirationMs()
        )
    }

    fun getCurrentUser(): UserResponse {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("No authentication found")
        val email = authentication.name

        logger.debug("Fetching current user: $email")

        val user = userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User not found: $email") }

        return UserResponse(
            id = user.id!!,
            name = user.name,
            email = user.email,
            createdAt = user.createdAt!!
        )
    }
}
