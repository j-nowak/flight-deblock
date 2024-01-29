package org.deblock.flights.service.client.toughjet

import java.time.Instant

data class ToughJetFlight(
    val carrier: String,
    val basePrice: Double,
    val tax: Double,
    val discount: Double,
    val departureAirportName: String,
    val arrivalAirportName: String,
    val outboundDateTime: Instant,
    val inboundDateTime: Instant
)