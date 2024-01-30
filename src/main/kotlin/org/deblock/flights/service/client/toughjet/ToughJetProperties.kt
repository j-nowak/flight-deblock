package org.deblock.flights.service.client.toughjet

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "suppliers.toughjet")
data class ToughJetProperties(
    val url: String,
)
