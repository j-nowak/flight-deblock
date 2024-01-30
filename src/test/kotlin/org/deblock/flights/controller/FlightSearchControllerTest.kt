package org.deblock.flights.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.deblock.flights.AbstractIntegrationTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@AutoConfigureMockMvc
class FlightSearchControllerTest(
    private val mockMvc: MockMvc,
) : AbstractIntegrationTest() {
    @Test
    fun `returns flight data from a single supplier if other are empty`() {
        stubCrazyAir()
        stubEmptyResponse("/toughjet/search")

        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights/search")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.flights").isArray)
            .andExpect(jsonPath("$.flights", hasSize<Any>(2)))
            .andExpect(jsonPath("$.flights[0].airline").value("British Airways"))
            .andExpect(jsonPath("$.flights[0].supplier").value("CrazyAir"))
            .andExpect(jsonPath("$.flights[0].fare").value(350.0))
            .andExpect(jsonPath("$.flights[0].departureAirportCode").value("LHR"))
            .andExpect(jsonPath("$.flights[0].destinationAirportCode").value("AMS"))
            .andExpect(jsonPath("$.flights[0].departureDate").value("2022-01-01T10:00:00"))
            .andExpect(jsonPath("$.flights[0].arrivalDate").value("2022-01-01T12:00:00"))
            .andExpect(jsonPath("$.flights[1].airline").value("Lufthansa"))
            .andExpect(jsonPath("$.flights[1].supplier").value("CrazyAir"))
            .andExpect(jsonPath("$.flights[1].fare").value(500.0))
            .andExpect(jsonPath("$.flights[1].departureAirportCode").value("LHR"))
            .andExpect(jsonPath("$.flights[1].destinationAirportCode").value("AMS"))
            .andExpect(jsonPath("$.flights[1].departureDate").value("2022-01-01T14:00:00"))
            .andExpect(jsonPath("$.flights[1].arrivalDate").value("2022-01-01T16:00:00"))
    }

    @Test
    fun `returns flight data from suppliers sorted by fare`() {
        stubCrazyAir()
        stubToughJet()

        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights/search")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.flights").isArray)
            .andExpect(jsonPath("$.flights", hasSize<Any>(3)))
            .andExpect(jsonPath("$.flights[0].airline").value("British Airways"))
            .andExpect(jsonPath("$.flights[0].supplier").value("CrazyAir"))
            .andExpect(jsonPath("$.flights[0].fare").value(BigDecimal("350.0")))
            .andExpect(jsonPath("$.flights[0].departureAirportCode").value("LHR"))
            .andExpect(jsonPath("$.flights[0].destinationAirportCode").value("AMS"))
            .andExpect(jsonPath("$.flights[0].departureDate").value("2022-01-01T10:00:00"))
            .andExpect(jsonPath("$.flights[0].arrivalDate").value("2022-01-01T12:00:00"))
            .andExpect(jsonPath("$.flights[1].airline").value("Lufthansa"))
            .andExpect(jsonPath("$.flights[1].supplier").value("ToughJet"))
            .andExpect(jsonPath("$.flights[1].fare").value(BigDecimal("360.0")))
            .andExpect(jsonPath("$.flights[1].departureAirportCode").value("LHR"))
            .andExpect(jsonPath("$.flights[1].destinationAirportCode").value("AMS"))
            .andExpect(jsonPath("$.flights[1].departureDate").value("2022-01-01T10:00:00"))
            .andExpect(jsonPath("$.flights[1].arrivalDate").value("2022-01-01T15:30:00"))
            .andExpect(jsonPath("$.flights[2].airline").value("Lufthansa"))
            .andExpect(jsonPath("$.flights[2].supplier").value("CrazyAir"))
            .andExpect(jsonPath("$.flights[2].fare").value(BigDecimal("500.0")))
            .andExpect(jsonPath("$.flights[2].departureAirportCode").value("LHR"))
            .andExpect(jsonPath("$.flights[2].destinationAirportCode").value("AMS"))
            .andExpect(jsonPath("$.flights[2].departureDate").value("2022-01-01T14:00:00"))
            .andExpect(jsonPath("$.flights[2].arrivalDate").value("2022-01-01T16:00:00"))
    }

    @Test
    fun `returns flight data from a single supplier if other exceeds timeout`() {
        stubCrazyAir(50000)
        stubToughJet(1000)

        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights/search")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.flights").isArray)
            .andExpect(jsonPath("$.flights", hasSize<Any>(1)))
            .andExpect(jsonPath("$.flights[0].airline").value("Lufthansa"))
            .andExpect(jsonPath("$.flights[0].supplier").value("ToughJet"))
    }

    @Test
    fun `returns empty result when suppliers don't have any matching flight`() {
        stubEmptyResponse("/crazyair/search")
        stubEmptyResponse("/toughjet/search")

        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights/search")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.flights").isEmpty)
    }

    private fun stubCrazyAir(delayInMillis: Int = 0) {
        wireMockServer.stubFor(
            get(WireMock.urlPathEqualTo("/crazyair/search"))
                .withQueryParam("origin", equalTo("LHR"))
                .withQueryParam("destination", equalTo("AMS"))
                .withQueryParam("departureDate", equalTo("2024-02-01"))
                .withQueryParam("returnDate", equalTo("2024-02-10"))
                .withQueryParam("passengerCount", equalTo("2"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withFixedDelay(delayInMillis)
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
                                    "price": 500.0,
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
    }

    private fun stubToughJet(delayInMillis: Int = 0) {
        wireMockServer.stubFor(
            get(WireMock.urlPathEqualTo("/toughjet/search"))
                .withQueryParam("from", equalTo("LHR"))
                .withQueryParam("to", equalTo("AMS"))
                .withQueryParam("outboundDate", equalTo("2024-02-01"))
                .withQueryParam("inboundDate", equalTo("2024-02-10"))
                .withQueryParam("numberOfAdults", equalTo("2"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withFixedDelay(delayInMillis)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            [
                                {
                                    "carrier": "Lufthansa",
                                    "basePrice": 350.0,
                                    "tax": 50.0,
                                    "discount": 10,
                                    "departureAirportName": "LHR",
                                    "arrivalAirportName": "AMS",
                                    "outboundDateTime": "2022-01-01T10:00:00Z",
                                    "inboundDateTime": "2022-01-01T15:30:00Z"
                                }
                            ]
                            """.trimIndent(),
                        ),
                ),
        )
    }

    private fun stubEmptyResponse(supplierUrl: String) {
        wireMockServer.stubFor(
            get(WireMock.urlPathEqualTo(supplierUrl))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]"),
                ),
        )
    }
}
