package org.deblock.flights.service

import kotlinx.coroutines.*
import org.deblock.flights.service.supplier.FlightSearchSupplier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import kotlin.time.Duration

@Service
class FlightSearchService(
    private val flightSearchSuppliers: List<FlightSearchSupplier>,
    private val supplierTimeout: Duration,
) {
    suspend fun searchFlights(request: FlightSearchRequest): List<Flight> = coroutineScope {
        val deferredFlights = flightSearchSuppliers.map { supplier ->
            async(Dispatchers.IO) {
                try {
                    withTimeout(supplierTimeout) {
                        supplier.searchFlights(request)
                    }
                } catch (e: Exception) {
                    // TODO: It would be good to inform the user, that some supplier failed and we return partial result.
                    logger.error("Error while fetching flights from ${supplier.javaClass.simpleName}", e)
                    emptyList()
                }
            }
        }

        val allFlights = deferredFlights.awaitAll().flatten().sortedBy { it.fare }

        return@coroutineScope allFlights
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FlightSearchService::class.java)
    }
}
