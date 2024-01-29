package org.deblock.flights.service.supplier.crazyair

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.format.DateTimeFormatter

@Service
class CrazyAirClient(private val restTemplate: RestTemplate, private val crazyAirProperties: CrazyAirProperties) {

    fun searchFlights(request: CrazyAirSearchRequest): List<CrazyAirFlight> {
        val url = buildUri(request)

        val responseEntity: ResponseEntity<Array<CrazyAirFlight>> = restTemplate.getForEntity(
            url,
            Array<CrazyAirFlight>::class.java
        )

        return responseEntity.body?.map { it } ?: emptyList()
    }

    private fun buildUri(request: CrazyAirSearchRequest): URI {
        val uriBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(crazyAirProperties.url)
            .queryParam("origin", request.origin)
            .queryParam("destination", request.destination)
            .queryParam("departureDate", request.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .queryParam("returnDate", request.returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .queryParam("passengerCount", request.passengerCount)

        return uriBuilder.build().toUri()
    }
}