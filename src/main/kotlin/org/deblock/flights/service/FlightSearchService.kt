package org.deblock.flights.service

import kotlinx.coroutines.*
import org.deblock.flights.service.supplier.FlightSearchSupplier
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class FlightSearchService(
    private val flightSearchSuppliers: List<FlightSearchSupplier>,
    private val supplierTimeout: Duration,
) {
    suspend fun searchFlights(request: FlightSearchRequest): List<Flight> = coroutineScope {
        val deferredFlights = flightSearchSuppliers.map { supplier ->
            async {
                withTimeout(supplierTimeout) {
                    supplier.searchFlights(request)
                }
            }
        }

        val allFlights = deferredFlights.awaitAll().flatten().sortedBy { it.fare }

        return@coroutineScope allFlights
    }
}
