package org.deblock.flights.controller.dto

import java.time.LocalDateTime

data class FlightSearchResponseDTO(val flights: List<FlightDTO>)

data class FlightDTO(
    val airline: String,
    val supplier: String,
    val fare: Double,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
)
