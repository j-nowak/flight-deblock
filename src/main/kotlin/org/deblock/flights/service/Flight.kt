package org.deblock.flights.service

import java.time.Instant

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: Double,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: Instant,
    val arrivalDate: Instant,
)
