package org.deblock.flights.service.supplier

import org.deblock.flights.service.Flight
import org.deblock.flights.service.FlightSearchRequest

interface FlightSearchSupplier {
    suspend fun searchFlights(request: FlightSearchRequest): List<Flight>
}
