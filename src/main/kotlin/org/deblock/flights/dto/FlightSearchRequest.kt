package org.deblock.flights.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class FlightSearchRequest(
    @NotBlank(message = "Origin is required")
    @Size(min = 3, max = 3, message = "Origin should be a 3-letter IATA code")
    val origin: String,

    @NotBlank(message = "Destination is required")
    @Size(min = 3, max = 3, message = "Destination should be a 3-letter IATA code")
    val destination: String,

    @NotBlank(message = "Departure date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val departureDate: LocalDate,

    @NotBlank(message = "Return date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val returnDate: LocalDate,

    @Min(value = 1, message = "Number of passengers should be at least 1")
    @Max(value = 4, message = "Number of passengers cannot exceed 4")
    val numberOfPassengers: Int
)