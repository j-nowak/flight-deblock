package org.deblock.flights.service.supplier.toughjet

import java.time.LocalDate

data class ToughJetSearchRequest(
    val from: String,
    val to: String,
    val outboundDate: LocalDate,
    val inboundDate: LocalDate,
    val numberOfAdults: Int
)