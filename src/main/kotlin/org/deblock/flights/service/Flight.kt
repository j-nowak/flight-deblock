package org.deblock.flights.service

import java.math.BigDecimal
import java.time.Instant

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: Instant,
    val arrivalDate: Instant,
)
