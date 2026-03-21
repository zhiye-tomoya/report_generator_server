package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

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

    @Test
    fun `should return true when email exists`() {
        val user = User(
            name = "testuser",
            email = "test@example.com",
            password = "password123"     
        )
        userRepository.save(user)

        val exists = userRepository.existsByEmail("test@example.com")

        assertThat(exists).isTrue()    
    }

    @Test
    fun `should return false when email does not exist`(){

        val exists = userRepository.existsByEmail("test@exapmle.com")

        assertThat(exists).isFalse()   
    }

@Test
fun `should return empty optional when user not found by email`(){

     val found = userRepository.findByEmail("test@exapmle.com")

     assertThat(found).isEmpty
}

}