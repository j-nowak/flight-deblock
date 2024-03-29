package org.deblock.flights.service.client.crazyair

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.assertj.core.api.Assertions.assertThat
import org.deblock.flights.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CrazyAirClientTest(
    private val crazyAirClient: CrazyAirClient,
) : AbstractIntegrationTest() {
    @Test
    fun `searchFlights should return a list of CrazyAirFlight`() {
        // Given
        val searchRequest =
            CrazyAirSearchRequest(
                origin = "LHR",
                destination = "AMS",
                departureDate = LocalDateTime.parse("2022-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
                returnDate = LocalDateTime.parse("2022-01-10T15:30:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
                passengerCount = 2,
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
                                    "departureDate": "2022-01-01T10:00:00",
                                    "arrivalDate": "2022-01-01T12:00:00"
                                },
                                {
                                    "airline": "Lufthansa",
                                    "price": 400.0,
                                    "cabinclass": "B",
                                    "departureAirportCode": "LHR",
                                    "destinationAirportCode": "AMS",
                                    "departureDate": "2022-01-01T14:00:00",
                                    "arrivalDate": "2022-01-01T16:00:00"
                                }
                            ]
                            """.trimIndent(),
                        ),
                ),
        )

        // When
        val result: List<CrazyAirFlight> = crazyAirClient.searchFlights(searchRequest)

        // Then
        assertThat(result).hasSize(2)

        // Assertions for the first CrazyAirFlight
        assertThat(result[0].airline).isEqualTo("British Airways")
        assertThat(result[0].price).isEqualTo(BigDecimal("350.0"))
        assertThat(result[0].cabinclass).isEqualTo("E")
        assertThat(result[0].departureAirportCode).isEqualTo("LHR")
        assertThat(result[0].destinationAirportCode).isEqualTo("AMS")
        assertThat(result[0].departureDate).isEqualTo(LocalDateTime.parse("2022-01-01T10:00:00"))
        assertThat(result[0].arrivalDate).isEqualTo(LocalDateTime.parse("2022-01-01T12:00:00"))

        // Assertions for the second CrazyAirFlight
        assertThat(result[1].airline).isEqualTo("Lufthansa")
        assertThat(result[1].price).isEqualTo(BigDecimal("400.0"))
        assertThat(result[1].cabinclass).isEqualTo("B")
        assertThat(result[1].departureAirportCode).isEqualTo("LHR")
        assertThat(result[1].destinationAirportCode).isEqualTo("AMS")
        assertThat(result[1].departureDate).isEqualTo(LocalDateTime.parse("2022-01-01T14:00:00"))
        assertThat(result[1].arrivalDate).isEqualTo(LocalDateTime.parse("2022-01-01T16:00:00"))
    }

    @Test
    fun `searchFlights should return an empty list for no results`() {
        // Given
        val searchRequest =
            CrazyAirSearchRequest(
                origin = "LHR",
                destination = "AMS",
                departureDate = LocalDateTime.parse("2022-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
                returnDate = LocalDateTime.parse("2022-01-10T15:30:00", DateTimeFormatter.ISO_DATE_TIME).toLocalDate(),
                passengerCount = 2,
            )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/crazyair/search"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]"),
                ),
        )

        // When
        val result: List<CrazyAirFlight> = crazyAirClient.searchFlights(searchRequest)

        // Then
        assertThat(result).isEmpty()
    }
}
