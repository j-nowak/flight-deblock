package org.deblock.flights.service.supplier.crazyair

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "crazyair")
data class CrazyAirProperties(
    val url: String
)