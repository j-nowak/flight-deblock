package org.deblock.flights.service

import org.springframework.stereotype.Service

@Service
class FlightSearchService {
    fun searchFlights(request: FlightSearchRequest): List<Flight> {
        return emptyList()
    }
}