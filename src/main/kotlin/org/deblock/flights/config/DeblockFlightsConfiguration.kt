package org.deblock.flights.config

import org.deblock.flights.service.FlightSearchService
import org.deblock.flights.service.client.crazyair.CrazyAirProperties
import org.deblock.flights.service.client.toughjet.ToughJetProperties
import org.deblock.flights.service.supplier.CrazyAirSupplier
import org.deblock.flights.service.supplier.ToughJetSupplier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import kotlin.time.Duration.Companion.seconds

@Configuration
@EnableConfigurationProperties(value = [CrazyAirProperties::class, ToughJetProperties::class])
class DeblockFlightsConfiguration(
    @Value("\${suppliers.timeout}") private val suppliersTimeout: Int,
) {
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
            suppliersTimeout.seconds,
        )
    }
}
