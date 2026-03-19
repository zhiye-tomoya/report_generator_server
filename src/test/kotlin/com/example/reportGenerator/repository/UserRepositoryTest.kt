package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.assertj.core.api.Assertions.assertThat

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should find user by email`() {
        // Given
        val user = User(
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword"
        )
        userRepository.save(user)

        // When
        val found = userRepository.findByEmail("test@example.com")

        // Then
        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("testuser")
    }

    @Test
    fun `should check if user exists by email`() {

    }

    @Test
    fun `should check if user exists by username`() {

    }
}
