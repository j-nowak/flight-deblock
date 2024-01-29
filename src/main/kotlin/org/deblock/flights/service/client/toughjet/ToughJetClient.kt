package org.deblock.flights.service.client.toughjet

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class ToughJetClient(private val restTemplate: RestTemplate, private val toughJetProperties: ToughJetProperties) {

    fun searchFlights(request: ToughJetSearchRequest): List<ToughJetFlight> {
        val url = buildUri(request)

        val responseEntity: ResponseEntity<Array<ToughJetFlight>> = restTemplate.getForEntity(
            url,
            Array<ToughJetFlight>::class.java
        )

        return responseEntity.body?.map { it } ?: emptyList()
    }

    private fun buildUri(request: ToughJetSearchRequest): URI {
        val uriBuilder: UriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(toughJetProperties.url)
            .queryParam("from", request.from)
            .queryParam("to", request.to)
            .queryParam("outboundDate", request.outboundDate)
            .queryParam("inboundDate", request.inboundDate)
            .queryParam("numberOfAdults", request.numberOfAdults)

        return uriBuilder.build().toUri()
    }
}