package org.deblock.flights.service.supplier.crazyair

import java.time.Instant

data class CrazyAirFlight(
    val airline: String,
    val price: Double,
    val cabinclass: String,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: Instant,
    val arrivalDate: Instant
)