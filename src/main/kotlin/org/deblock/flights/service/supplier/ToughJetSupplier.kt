package org.deblock.flights.service.supplier

import org.deblock.flights.service.Flight
import org.deblock.flights.service.FlightSearchRequest
import org.deblock.flights.service.client.toughjet.ToughJetClient
import org.deblock.flights.service.client.toughjet.ToughJetFlight
import org.deblock.flights.service.client.toughjet.ToughJetSearchRequest
import org.springframework.stereotype.Service

@Service
class ToughJetSupplier(private val toughJetClient: ToughJetClient) : FlightSearchSupplier {

    override suspend fun searchFlights(request: FlightSearchRequest): List<Flight> {
        val toughJetSearchRequest = ToughJetSearchRequest(
            from = request.origin,
            to = request.destination,
            outboundDate = request.departureDate,
            inboundDate = request.returnDate,
            numberOfAdults = request.numberOfPassengers
        )

        val toughJetFlights = toughJetClient.searchFlights(toughJetSearchRequest)

        return toughJetFlights.map { toughJetFlight ->
            mapToFlight(toughJetFlight)
        }
    }

    private fun mapToFlight(toughJetFlight: ToughJetFlight): Flight {
        return Flight(
            airline = toughJetFlight.carrier,
            supplier = TOUGH_JET_SUPPLIER,
            fare = calculateFare(toughJetFlight.basePrice, toughJetFlight.tax, toughJetFlight.discount),
            departureAirportCode = toughJetFlight.departureAirportName,
            destinationAirportCode = toughJetFlight.arrivalAirportName,
            departureDate = toughJetFlight.outboundDateTime,
            arrivalDate = toughJetFlight.inboundDateTime,
        )
    }

    private fun calculateFare(basePrice: Double, tax: Double, discount: Double): Double {
        return (basePrice + tax) * (1 - discount / 100)
    }


    companion object {
        const val TOUGH_JET_SUPPLIER = "ToughJet"
    }
}
