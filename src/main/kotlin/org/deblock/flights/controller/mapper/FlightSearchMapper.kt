package org.deblock.flights.controller.mapper

import org.deblock.flights.controller.dto.FlightDTO
import org.deblock.flights.controller.dto.FlightSearchResponseDTO
import org.deblock.flights.service.Flight
import java.time.LocalDateTime
import java.time.ZoneOffset

object FlightSearchMapper {
    fun map(searchResult: List<Flight>): FlightSearchResponseDTO {
        return FlightSearchResponseDTO(
            searchResult.map { map(it) },
        )
    }

    private fun map(flight: Flight): FlightDTO {
        return FlightDTO(
            flight.airline,
            flight.supplier,
            flight.fare,
            flight.departureAirportCode,
            flight.destinationAirportCode,
            LocalDateTime.ofInstant(flight.departureDate, ZoneOffset.UTC),
            LocalDateTime.ofInstant(flight.arrivalDate, ZoneOffset.UTC),
        )
    }
}
