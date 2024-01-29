package org.deblock.flights.service.supplier.crazyair

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.deblock.flights.service.supplier.toughjet.ToughJetClient
import org.deblock.flights.service.supplier.toughjet.ToughJetFlight
import org.deblock.flights.service.supplier.toughjet.ToughJetSearchRequest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToughJetClientTest(
    private val toughJetClient: ToughJetClient
) {
    private val wireMockServer : WireMockServer = WireMockServer(8095)

    @BeforeAll
    fun beforeAll() {
        wireMockServer.start()
    }

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

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
        assertThat(result[0].carrier).isEqualTo("ToughJet")
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
