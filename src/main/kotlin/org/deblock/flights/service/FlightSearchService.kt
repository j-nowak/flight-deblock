package org.deblock.flights.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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
    fun searchFlights(request: FlightSearchRequest): List<Flight> {
        return runBlocking {
            val deferredFlights =
                flightSearchSuppliers.map { supplier ->
                    async(Dispatchers.IO) {
                        try {
                            withTimeout(supplierTimeout) {
                                logger.info("Sending a request to supplier ${supplier.javaClass.simpleName}")
                                val result = supplier.searchFlights(request)
                                logger.info("Got result from ${supplier.javaClass.simpleName}")
                                result
                            }
                        } catch (e: Exception) {
                            // TODO: It would be good to inform the user, that some supplier failed and we return partial result.
                            logger.error("Error while fetching flights from ${supplier.javaClass.simpleName}", e)
                            emptyList()
                        }
                    }
                }

            deferredFlights.awaitAll().flatten().sortedBy { it.fare }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FlightSearchService::class.java)
    }
}
