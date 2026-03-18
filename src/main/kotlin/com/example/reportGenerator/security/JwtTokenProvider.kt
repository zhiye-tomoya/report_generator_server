package com.example.reportGenerator.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,
    
    @Value("\${jwt.expiration}")
    private val jwtExpirationMs: Long,
    
    @Value("\${jwt.refresh-expiration}")
    private val jwtRefreshExpirationMs: Long
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        return generateTokenFromEmail(userPrincipal.username)
    }

    fun generateTokenFromEmail(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtRefreshExpirationMs)

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun getEmailFromToken(token: String): String {
        val claims = getClaims(token)
        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authToken)
            return true
        } catch (e: Exception) {
            logger.error("JWT validation error: ${e.message}")
        }
        return false
    }

    fun getExpirationMs(): Long = jwtExpirationMs

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
