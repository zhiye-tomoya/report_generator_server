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

    @Test
    fun `should find user by email`() {
        // Given
        val user = User(
            name = "testuser",
            email = "test@example.com",
            password = "hashedpassword"
        )
        userRepository.save(user)

        // When
        val found = userRepository.findByEmail("test@example.com")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("testuser")
    }

}
