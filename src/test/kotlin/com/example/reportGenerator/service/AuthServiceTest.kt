package com.example.reportGenerator.service

import com.example.reportGenerator.dto.LoginRequest
import com.example.reportGenerator.dto.RegisterRequest
import com.example.reportGenerator.entity.User
import com.example.reportGenerator.exception.ResourceAlreadyExistsException
import com.example.reportGenerator.repository.UserRepository
import com.example.reportGenerator.security.JwtTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val authenticationManager: AuthenticationManager = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()

    private val authService = AuthService(
        userRepository,
        passwordEncoder,
        authenticationManager,
        jwtTokenProvider
    )

    @Test
    fun `should normalize email to lowercase during registration`() {
        // Given
        val request = RegisterRequest(
            name = "Test User",
            email = "Test@Example.COM",
            password = "password123"
        )
        val normalizedEmail = "test@example.com"
        val encodedPassword = "encodedPassword123"

        whenever(userRepository.existsByEmail(normalizedEmail)).thenReturn(false)
        whenever(passwordEncoder.encode(request.password)).thenReturn(encodedPassword)
        whenever(userRepository.save(any<User>())).thenAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            user.copy(id = 1L, createdAt = LocalDateTime.now())
        }

        // When
        val response = authService.register(request)

        // Then
        assertThat(response.email).isEqualTo(normalizedEmail)
        verify(userRepository).existsByEmail(normalizedEmail)
        verify(userRepository).save(argThat { user ->
            user.email == normalizedEmail &&
            user.name == "Test User" &&
            user.password == encodedPassword
        })
    }

    @Test
    fun `should check for duplicate email using normalized lowercase email`() {
        // Given
        val request = RegisterRequest(
            name = "Test User",
            email = "Test@Example.COM",
            password = "password123"
        )
        val normalizedEmail = "test@example.com"

        whenever(userRepository.existsByEmail(normalizedEmail)).thenReturn(true)

        // When & Then
        val exception = assertThrows<ResourceAlreadyExistsException> {
            authService.register(request)
        }

        assertThat(exception.message).contains(normalizedEmail)
        verify(userRepository).existsByEmail(normalizedEmail)
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `should prevent registration with same email in different cases`() {
        // Given - 既に "user@example.com" が登録されている状況
        whenever(userRepository.existsByEmail("user@example.com")).thenReturn(true)

        val request = RegisterRequest(
            name = "New User",
            email = "User@Example.COM", // 大文字混じり
            password = "password123"
        )

        // When & Then - 正規化後に重複が検出される
        assertThrows<ResourceAlreadyExistsException> {
            authService.register(request)
        }

        verify(userRepository).existsByEmail("user@example.com")
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `should normalize email to lowercase during login`() {
        // Given
        val request = LoginRequest(
            email = "Test@Example.COM",
            password = "password123"
        )
        val normalizedEmail = "test@example.com"
        val mockAuthentication: Authentication = mock()

        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
        whenever(jwtTokenProvider.generateToken(mockAuthentication)).thenReturn("test-token")
        whenever(jwtTokenProvider.generateRefreshToken(normalizedEmail)).thenReturn("refresh-token")
        whenever(jwtTokenProvider.getExpirationMs()).thenReturn(3600000L)

        // When
        val response = authService.login(request)

        // Then
        verify(authenticationManager).authenticate(
            argThat { token ->
                token is UsernamePasswordAuthenticationToken &&
                token.principal == normalizedEmail &&
                token.credentials == "password123"
            }
        )
        verify(jwtTokenProvider).generateRefreshToken(normalizedEmail)
        assertThat(response.token).isEqualTo("test-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
    }

    @Test
    fun `should allow login with mixed case email`() {
        // Given - ユーザーは "user@example.com" で登録されている
        val request = LoginRequest(
            email = "User@Example.COM", // 大文字混じりでログイン試行
            password = "password123"
        )
        val normalizedEmail = "user@example.com"
        val mockAuthentication: Authentication = mock()

        whenever(authenticationManager.authenticate(any())).thenReturn(mockAuthentication)
        whenever(jwtTokenProvider.generateToken(mockAuthentication)).thenReturn("test-token")
        whenever(jwtTokenProvider.generateRefreshToken(normalizedEmail)).thenReturn("refresh-token")
        whenever(jwtTokenProvider.getExpirationMs()).thenReturn(3600000L)

        // When
        val response = authService.login(request)

        // Then - 正規化されたメールアドレスで認証される
        verify(authenticationManager).authenticate(
            argThat { token ->
                token is UsernamePasswordAuthenticationToken &&
                token.principal == normalizedEmail
            }
        )
        assertThat(response.token).isNotNull()
    }

    @Test
    fun `should register user successfully with all lowercase email`() {
        // Given
        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )
        val encodedPassword = "encodedPassword123"
        val createdAt = LocalDateTime.now()

        whenever(userRepository.existsByEmail("test@example.com")).thenReturn(false)
        whenever(passwordEncoder.encode(request.password)).thenReturn(encodedPassword)
        whenever(userRepository.save(any<User>())).thenReturn(
            User(
                id = 1L,
                name = "Test User",
                email = "test@example.com",
                password = encodedPassword,
                createdAt = createdAt
            )
        )

        // When
        val response = authService.register(request)

        // Then
        assertThat(response.id).isEqualTo(1L)
        assertThat(response.name).isEqualTo("Test User")
        assertThat(response.email).isEqualTo("test@example.com")
        assertThat(response.createdAt).isEqualTo(createdAt)
    }
}
