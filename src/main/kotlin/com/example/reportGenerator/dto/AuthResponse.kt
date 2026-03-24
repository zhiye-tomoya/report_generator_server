package com.example.reportGenerator.dto

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val expiresIn: Long
)
