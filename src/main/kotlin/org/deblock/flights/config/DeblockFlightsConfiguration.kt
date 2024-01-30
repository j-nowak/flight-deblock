package org.deblock.flights.config

import org.deblock.flights.service.FlightSearchService
import org.deblock.flights.service.client.crazyair.CrazyAirProperties
import org.deblock.flights.service.client.toughjet.ToughJetProperties
import org.deblock.flights.service.supplier.CrazyAirSupplier
import org.deblock.flights.service.supplier.ToughJetSupplier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

@Configuration
@EnableConfigurationProperties(value = [CrazyAirProperties::class, ToughJetProperties::class])
class DeblockFlightsConfiguration(
    @Value("\${suppliers.timeout}") private val suppliersTimeout: Long,
) {
    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        val factory =
            SimpleClientHttpRequestFactory().apply {
                setReadTimeout(Duration.ofSeconds(suppliersTimeout))
            }
        return RestTemplate(factory)
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
