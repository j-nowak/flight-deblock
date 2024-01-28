package org.deblock.flights.dto

import java.time.Instant

data class FlightSearchResponse(val flights: List<Flight>)

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: Double,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: Instant,
    val arrivalDate: Instant
)