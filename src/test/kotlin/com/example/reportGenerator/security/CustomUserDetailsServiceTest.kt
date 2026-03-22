package com.example.reportGenerator.security

import com.example.reportGenerator.entity.User
import com.example.reportGenerator.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDateTime
import java.util.*

class CustomUserDetailsServiceTest {

    private val userRepository: UserRepository = mock()
    private val userDetailsService = CustomUserDetailsService(userRepository)

    @Test
    fun `should normalize email to lowercase when loading user`() {
        // Given
        val mixedCaseEmail = "Test@Example.COM"
        val normalizedEmail = "test@example.com"
        val user = User(
            id = 1L,
            name = "Test User",
            email = normalizedEmail,
            password = "encodedPassword",
            createdAt = LocalDateTime.now()
        )

        whenever(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(user))

        // When
        val userDetails = userDetailsService.loadUserByUsername(mixedCaseEmail)

        // Then
        verify(userRepository).findByEmail(normalizedEmail)
        assertThat(userDetails.username).isEqualTo(normalizedEmail)
        assertThat(userDetails.password).isEqualTo("encodedPassword")
    }

    @Test
    fun `should load user with lowercase email`() {
        // Given
        val email = "test@example.com"
        val user = User(
            id = 1L,
            name = "Test User",
            email = email,
            password = "encodedPassword",
            createdAt = LocalDateTime.now()
        )

        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // When
        val userDetails = userDetailsService.loadUserByUsername(email)

        // Then
        assertThat(userDetails.username).isEqualTo(email)
        assertThat(userDetails.password).isEqualTo("encodedPassword")
        assertThat(userDetails.authorities).isEmpty()
    }

    @Test
    fun `should throw UsernameNotFoundException when user not found`() {
        // Given
        val email = "nonexistent@example.com"
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername(email)
        }

        assertThat(exception.message).contains(email)
    }

    @Test
    fun `should find user regardless of email case`() {
        // Given - ユーザーは小文字で保存されている
        val savedEmail = "user@example.com"
        val user = User(
            id = 1L,
            name = "Test User",
            email = savedEmail,
            password = "encodedPassword",
            createdAt = LocalDateTime.now()
        )

        whenever(userRepository.findByEmail(savedEmail)).thenReturn(Optional.of(user))

        // When - 大文字混じりでロード試行
        val userDetails = userDetailsService.loadUserByUsername("User@Example.COM")

        // Then - 正規化されて正しく見つかる
        verify(userRepository).findByEmail(savedEmail)
        assertThat(userDetails.username).isEqualTo(savedEmail)
    }

    @Test
    fun `should throw exception with normalized email when user not found with mixed case`() {
        // Given
        val mixedCaseEmail = "NonExistent@Example.COM"
        val normalizedEmail = "nonexistent@example.com"
        
        whenever(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername(mixedCaseEmail)
        }

        verify(userRepository).findByEmail(normalizedEmail)
        assertThat(exception.message).contains(normalizedEmail)
    }
}
