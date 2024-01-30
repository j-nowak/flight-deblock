package org.deblock.flights.config

import org.deblock.flights.service.FlightSearchService
import org.deblock.flights.service.client.crazyair.CrazyAirProperties
import org.deblock.flights.service.client.toughjet.ToughJetProperties
import org.deblock.flights.service.supplier.CrazyAirSupplier
import org.deblock.flights.service.supplier.ToughJetSupplier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Configuration
@EnableConfigurationProperties(value = [CrazyAirProperties::class, ToughJetProperties::class])
class DeblockFlightsConfiguration {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun flightSearchService(
        crazyAirSupplier: CrazyAirSupplier,
        toughJetSupplier: ToughJetSupplier,
    ): FlightSearchService {
        return FlightSearchService(
            listOf(crazyAirSupplier, toughJetSupplier),
            60.seconds,
        )
    }
}
