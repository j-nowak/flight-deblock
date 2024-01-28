package org.deblock.flights

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeblockFlightsApplication

fun main(args: Array<String>) {
    runApplication<DeblockFlightsApplication>(*args)
}
