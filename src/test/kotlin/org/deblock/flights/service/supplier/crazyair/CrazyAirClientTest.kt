package org.deblock.flights.service.supplier.crazyair

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrazyAirClientTest(
    private val crazyAirClient: CrazyAirClient
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
    fun `searchFlights should return a list of CrazyAirFlight`() {
        // Given
        val searchRequest = CrazyAirSearchRequest(
            origin = "LHR",
            destination = "AMS",
            departureDate = LocalDateTime.parse("2022-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
            returnDate = LocalDateTime.parse("2022-01-10T15:30:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
            passengerCount = 2
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/crazyair/search"))
                .withQueryParam("origin", equalTo(searchRequest.origin))
                .withQueryParam("destination", equalTo(searchRequest.destination))
                .withQueryParam("departureDate", equalTo(searchRequest.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .withQueryParam("returnDate", equalTo(searchRequest.returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .withQueryParam("passengerCount", equalTo(searchRequest.passengerCount.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            [
                                {
                                    "airline": "British Airways",
                                    "price": 350.0,
                                    "cabinclass": "E",
                                    "departureAirportCode": "LHR",
                                    "destinationAirportCode": "AMS",
                                    "departureDate": "2022-01-01T10:00:00Z",
                                    "arrivalDate": "2022-01-01T12:00:00Z"
                                },
                                {
                                    "airline": "Lufthansa",
                                    "price": 400.0,
                                    "cabinclass": "B",
                                    "departureAirportCode": "LHR",
                                    "destinationAirportCode": "AMS",
                                    "departureDate": "2022-01-01T14:00:00Z",
                                    "arrivalDate": "2022-01-01T16:00:00Z"
                                }
                            ]
                            """.trimIndent()
                        )
                )
        )

        // When
        val result: List<CrazyAirFlight> = crazyAirClient.searchFlights(searchRequest)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].airline).isEqualTo("British Airways")
        assertThat(result[1].airline).isEqualTo("Lufthansa")
    }

    @Test
    fun `searchFlights should return an empty list for no results`() {
        // Given
        val searchRequest = CrazyAirSearchRequest(
            origin = "LHR",
            destination = "AMS",
            departureDate = LocalDateTime.parse("2022-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
            returnDate = LocalDateTime.parse("2022-01-10T15:30:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
            passengerCount = 2
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/crazyair/search"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
                )
        )

        // When
        val result: List<CrazyAirFlight> = crazyAirClient.searchFlights(searchRequest)

        // Then
        assertThat(result).isEmpty()
    }
}
