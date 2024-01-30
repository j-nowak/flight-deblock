package org.deblock.flights.service.client.crazyair

import java.math.BigDecimal
import java.time.LocalDateTime

data class CrazyAirFlight(
    val airline: String,
    val price: BigDecimal,
    val cabinclass: String,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
)
