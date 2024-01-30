package org.deblock.flights.service.client.crazyair

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "suppliers.crazyair")
data class CrazyAirProperties(
    val url: String,
)
