package com.example.reportGenerator.entity

import jakarta.persistence.*
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

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)
