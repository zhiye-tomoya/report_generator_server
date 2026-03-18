package com.example.reportGenerator.dto

import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime
)
