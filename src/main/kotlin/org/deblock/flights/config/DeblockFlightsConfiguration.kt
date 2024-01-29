package org.deblock.flights.config

import org.deblock.flights.service.client.crazyair.CrazyAirProperties
import org.deblock.flights.service.client.toughjet.ToughJetProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(value = [CrazyAirProperties::class, ToughJetProperties::class])
class DeblockFlightsConfiguration {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
