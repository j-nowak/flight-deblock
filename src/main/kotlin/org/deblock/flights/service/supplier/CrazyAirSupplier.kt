package org.deblock.flights.service.supplier

import org.deblock.flights.service.Flight
import org.deblock.flights.service.FlightSearchRequest
import org.deblock.flights.service.client.crazyair.CrazyAirClient
import org.deblock.flights.service.client.crazyair.CrazyAirFlight
import org.deblock.flights.service.client.crazyair.CrazyAirSearchRequest
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.stream.Collectors

@Service
class CrazyAirSupplier(private val crazyAirClient: CrazyAirClient) : FlightSearchSupplier {

    override suspend fun searchFlights(request: FlightSearchRequest): List<Flight> {
        val crazyAirSearchRequest = mapToCrazyAirSearchRequest(request)
        val crazyAirFlights = crazyAirClient.searchFlights(crazyAirSearchRequest)
        return mapToFlights(crazyAirFlights)
    }

    private fun mapToCrazyAirSearchRequest(request: FlightSearchRequest): CrazyAirSearchRequest {
        return CrazyAirSearchRequest(
            origin = request.origin,
            destination = request.destination,
            departureDate = request.departureDate,
            returnDate = request.returnDate,
            passengerCount = request.numberOfPassengers
        )
    }

    private fun mapToFlights(crazyAirFlights: List<CrazyAirFlight>): List<Flight> {
        return crazyAirFlights.stream()
            .map { crazyAirFlight ->
                Flight(
                    airline = crazyAirFlight.airline,
                    supplier = CRAZY_AIR_SUPPLIER,
                    fare = crazyAirFlight.price,
                    departureAirportCode = crazyAirFlight.departureAirportCode,
                    destinationAirportCode = crazyAirFlight.destinationAirportCode,
                    departureDate = crazyAirFlight.departureDate.atOffset(ZoneOffset.UTC).toInstant(),
                    arrivalDate = crazyAirFlight.arrivalDate.atOffset(ZoneOffset.UTC).toInstant(),
                )
            }
            .collect(Collectors.toList())
    }

    companion object {
        const val CRAZY_AIR_SUPPLIER = "CrazyAir"
    }
}
