package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    private fun createUser(
        name: String,
        email: String,
        password: String = "password123",
    ) = User(
        name = name,
        email = email,
        password = password,
    )

    @Test
    fun `should automatically lowercase email at the entity level`() {
        // Given
        val user = createUser(name = "testuser", email = "TEST@EXAMPLE.COM")

        // When
        val saved = userRepository.save(user)

        // Then
        assertThat(saved.email).isEqualTo("test@example.com")
    }

    @Test
    fun `should find user by email`() {
        // Given
        val user = createUser(name = "testuser", email = "test@example.com")
        userRepository.save(user)

        // When
        val found = userRepository.findByEmail("test@example.com")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("testuser")
        assertThat(found.get().email).isEqualTo("test@example.com")
    }

    @Test
    fun `should return empty optional when user not found by email`() {
        // When
        val found = userRepository.findByEmail("nonexistent@example.com")

        // Then
        assertThat(found).isEmpty()
    }

    @Test
    fun `should return true when email exists`() {
        // Given
        val user = createUser(name = "testuser", email = "test@example.com")
        userRepository.save(user)

        // When
        val exists = userRepository.existsByEmail("test@example.com")

        // Then
        assertThat(exists).isTrue()
    }

    @Test
    fun `should return false when email does not exist`() {
        // When
        val exists = userRepository.existsByEmail("nonexistent@example.com")

        // Then
        assertThat(exists).isFalse()
    }

    @Test
    fun `should save user with all required fields`() {
        // Given
        val user = createUser(name = "testuser", email = "test@example.com")

        // When
        val saved = userRepository.save(user)

        // Then
        assertThat(saved.id).isNotNull()
        assertThat(saved.createdAt).isNotNull()
        assertThat(saved.name).isEqualTo("testuser")
        assertThat(saved.email).isEqualTo("test@example.com")
        assertThat(saved.password).isEqualTo("password123")
    }

    @Test
    fun `should find user by normalized lowercase email`() {
        // Given
        val user = createUser(name = "testuser", email = "Test@Example.com")
        userRepository.save(user)

        // When & Then - 保存時に小文字化されるので、小文字で検索すれば見つかる
        assertThat(userRepository.findByEmail("test@example.com")).isPresent
        assertThat(userRepository.findByEmail("test@example.com").get().email)
            .isEqualTo("test@example.com")
        
        // 大文字で検索すると見つからない（Repository層では正規化しない）
        assertThat(userRepository.findByEmail("Test@Example.com")).isEmpty()
    }

    @Test
    fun `should find multiple users and verify uniqueness by email`() {
        // Given
        val user1 = createUser(name = "user1", email = "user1@example.com")
        val user2 = createUser(name = "user2", email = "user2@example.com")
        userRepository.save(user1)
        userRepository.save(user2)

        // When
        val foundUser1 = userRepository.findByEmail("user1@example.com")
        val foundUser2 = userRepository.findByEmail("user2@example.com")

        // Then
        assertThat(foundUser1).isPresent
        assertThat(foundUser2).isPresent
        assertThat(foundUser1.get().id).isNotEqualTo(foundUser2.get().id)
        assertThat(foundUser1.get().email).isEqualTo("user1@example.com")
        assertThat(foundUser2.get().email).isEqualTo("user2@example.com")
    }
}
