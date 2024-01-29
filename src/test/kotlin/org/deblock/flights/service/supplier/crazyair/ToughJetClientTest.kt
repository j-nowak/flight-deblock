package org.deblock.flights.service.supplier.crazyair

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.deblock.flights.AbstractIntegrationTest
import org.deblock.flights.service.client.toughjet.ToughJetClient
import org.deblock.flights.service.client.toughjet.ToughJetFlight
import org.deblock.flights.service.client.toughjet.ToughJetSearchRequest
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

class ToughJetClientTest(
    private val toughJetClient: ToughJetClient
) : AbstractIntegrationTest() {
    @Test
    fun `searchFlights should return a list of ToughJetFlight`() {
        // Given
        val outBoundDate = "2022-01-10"
        val inboundDate = "2022-01-10"
        val searchRequest = ToughJetSearchRequest(
            from = "LHR",
            to = "AMS",
            outboundDate = LocalDate.parse(outBoundDate),
            inboundDate = LocalDate.parse(inboundDate),
            numberOfAdults = 2
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/toughjet/search"))
                .withQueryParam("from", equalTo(searchRequest.from))
                .withQueryParam("to", equalTo(searchRequest.to))
                .withQueryParam("outboundDate", equalTo(outBoundDate))
                .withQueryParam("inboundDate", equalTo(inboundDate))
                .withQueryParam("numberOfAdults", equalTo(searchRequest.numberOfAdults.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            [
                                {
                                    "carrier": "ToughJet",
                                    "basePrice": 300.0,
                                    "tax": 50.0,
                                    "discount": 10,
                                    "departureAirportName": "LHR",
                                    "arrivalAirportName": "AMS",
                                    "outboundDateTime": "2022-01-01T10:00:00Z",
                                    "inboundDateTime": "2022-01-10T15:30:00Z"
                                }
                            ]
                            """.trimIndent()
                        )
                )
        )

        // When
        val result: List<ToughJetFlight> = toughJetClient.searchFlights(searchRequest)

        // Then
        assertThat(result).hasSize(1)
        val toughJetFlight = result[0]
        assertThat(toughJetFlight.carrier).isEqualTo("ToughJet")
        assertThat(toughJetFlight.basePrice).isEqualTo(300.0)
        assertThat(toughJetFlight.tax).isEqualTo(50.0)
        assertThat(toughJetFlight.discount).isEqualTo(10.0)
        assertThat(toughJetFlight.departureAirportName).isEqualTo("LHR")
        assertThat(toughJetFlight.arrivalAirportName).isEqualTo("AMS")
        assertThat(toughJetFlight.outboundDateTime).isEqualTo(Instant.parse("2022-01-01T10:00:00Z"))
        assertThat(toughJetFlight.inboundDateTime).isEqualTo(Instant.parse("2022-01-10T15:30:00Z"))
    }

    @Test
    fun `searchFlights should return an empty list for no results`() {
        // Given
        val searchRequest = ToughJetSearchRequest(
            from = "LHR",
            to = "AMS",
            outboundDate = LocalDate.parse("2022-01-10"),
            inboundDate = LocalDate.parse("2022-01-10"),
            numberOfAdults = 2
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/toughjet/search"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
                )
        )

        // When
        val result: List<ToughJetFlight> = toughJetClient.searchFlights(searchRequest)

        // Then
        assertThat(result).isEmpty()
    }
}
