package com.example.reportGenerator.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    fun handleResourceAlreadyExistsException(
        ex: ResourceAlreadyExistsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Resource already exists: ${ex.message}")
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflict",
            message = ex.message ?: "Resource already exists",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Resource not found: ${ex.message}")
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Resource not found",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Bad credentials: ${ex.message}")
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Unauthorized",
            message = "Invalid email or password",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        ex: UsernameNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("User not found: ${ex.message}")
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "User not found",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Validation error: ${ex.message}")
        
        val errors = ex.bindingResult.allErrors.associate { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            fieldName to errorMessage
        }
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "Validation failed",
            path = request.getDescription(false).replace("uri=", ""),
            validationErrors = errors
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ${ex.message}", ex)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            path = request.getDescription(false).replace("uri=", "")
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
