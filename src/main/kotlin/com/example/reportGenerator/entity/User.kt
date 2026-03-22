package com.example.reportGenerator.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @field:Email
    @field:NotBlank
    @field:Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false)
    val password: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
) {
    @PrePersist
    @PreUpdate
    fun prePersist() {
        email = email.lowercase()
    }
}
