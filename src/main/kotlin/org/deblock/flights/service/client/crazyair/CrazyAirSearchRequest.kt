package org.deblock.flights.service.client.crazyair

import java.time.LocalDate

data class CrazyAirSearchRequest(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val passengerCount: Int,
)
