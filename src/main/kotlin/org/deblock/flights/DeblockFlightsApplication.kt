package org.deblock.flights

import org.deblock.flights.config.DeblockFlightsConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(
    *[
        DeblockFlightsConfiguration::class
    ]
)
class DeblockFlightsApplication

fun main(args: Array<String>) {
    runApplication<DeblockFlightsApplication>(*args)
}
