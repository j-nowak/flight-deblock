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

@RestControllerAdvice
class ErrorHandlingAdvice {

    @ExceptionHandler(ConstraintViolationException::class, MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: Exception): ResponseEntity<ErrorResponse> {
        val details = when (ex) {
            is ConstraintViolationException -> ex.constraintViolations.map { it.message }
            is MethodArgumentNotValidException -> ex.bindingResult.fieldErrors.map { it.defaultMessage }
            else -> emptyList()
        }
        return ResponseEntity.badRequest().body(ErrorResponse.invalidRequestBody(details))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return when (val cause = ex.cause) {
            is ValueInstantiationException -> handleValueInstantiationException(cause)
            is MissingKotlinParameterException -> handleMissingParameter(cause)
            is InvalidFormatException -> handleInvalidFormatException(cause)
            else -> ResponseEntity.badRequest().body(ErrorResponse.invalidRequestBody())
        }
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.internalServerError().body(ErrorResponse.internalServerError())
    }

    private fun handleValueInstantiationException(ex: ValueInstantiationException): ResponseEntity<ErrorResponse> {
        val details = if (ex.cause is IllegalArgumentException && ex.cause?.message != null) {
            listOf((ex.cause as IllegalArgumentException).message!!)
        } else {
            emptyList()
        }
        return ResponseEntity.badRequest().body(
            ErrorResponse.invalidRequestBody(details)
        )
    }

    private fun handleMissingParameter(ex: MissingKotlinParameterException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
            ErrorResponse.invalidRequestBody(
                "Missing request body parameter: ${ex.path.first().fieldName}"
            )
        )
    }

    private fun handleInvalidFormatException(ex: InvalidFormatException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
            ErrorResponse.invalidRequestBody("Invalid format for parameter: ${ex.path.first().fieldName}")
        )
    }
}

data class ErrorResponse(
    val error: String,
    val details: List<String> = emptyList()
) {
    companion object {
        fun invalidRequestBody(errorDetails: List<String> = emptyList()): ErrorResponse {
            return ErrorResponse("Invalid request body", errorDetails)
        }

        fun invalidRequestBody(errorDetails: String): ErrorResponse {
            return invalidRequestBody(listOf(errorDetails))
        }

        fun internalServerError(): ErrorResponse {
            return ErrorResponse("Internal server error")
        }
    }
}
