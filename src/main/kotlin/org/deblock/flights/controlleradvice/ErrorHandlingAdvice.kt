package org.deblock.flights.controlleradvice

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.IllegalArgumentException

@RestControllerAdvice
class ErrorHandlingAdvice {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val details = ex.constraintViolations.map { it.message }
        val errorResponse = ErrorResponse("Invalid request body", details)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.map { it.defaultMessage ?: "Validation failed" }
        val errorResponse = ErrorResponse("Invalid request body", details)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        if (ex.cause is ValueInstantiationException) {
            return handleValueInstantiationException(ex.cause as ValueInstantiationException)
        } else if (ex.cause is MissingKotlinParameterException) {
            return handleMissingParameter(ex.cause as MissingKotlinParameterException)
        } else if (ex.cause is InvalidFormatException) {
            return handleInvalidFormatException(ex.cause as InvalidFormatException)
        }
//        val details = ex
        val errorResponse = ErrorResponse("Invalid request body")
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Internal server error")
        return ResponseEntity.internalServerError().body(errorResponse)
    }

    private fun handleValueInstantiationException(ex: ValueInstantiationException): ResponseEntity<ErrorResponse> {
        val errorResponse = if (ex.cause is IllegalArgumentException && ex.cause?.message != null) {
            val details = (ex.cause as IllegalArgumentException).message!!
            ErrorResponse("Invalid request body", listOf(details))
        } else {
            ErrorResponse("Invalid request body")
        }
        return ResponseEntity.badRequest().body(errorResponse)
    }

    private fun handleMissingParameter(ex: MissingKotlinParameterException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            "Invalid request body",
            listOf("Missing request body parameter: ${ex.path.first().fieldName}")
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    private fun handleInvalidFormatException(ex: InvalidFormatException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            "Invalid request body",
            listOf("Invalid format for parameter: ${ex.path.first().fieldName}")
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }
}

data class ErrorResponse(
    val error: String,
    val details: List<String> = emptyList()
)
