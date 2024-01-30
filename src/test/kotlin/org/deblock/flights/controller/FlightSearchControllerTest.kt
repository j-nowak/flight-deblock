package org.deblock.flights.controller

import org.deblock.flights.AbstractIntegrationTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class FlightSearchControllerTest(
    private val mockMvc: MockMvc,
) : AbstractIntegrationTest() {
    @Test
    fun `returns flight data from a single supplier if other are empty`() {
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
            .andExpect(jsonPath("$.suggestedFlights", hasSize<Any>(2)))
            .andExpect(jsonPath("$.flights[0].fare").exists())
            .andExpect(jsonPath("$.flights[1].fare").exists())
        // TODO: Validation here
    }

    @Test
    fun `returns flight data from suppliers sorted by fare`() {
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
            .andExpect(jsonPath("$.suggestedFlights", hasSize<Any>(2)))
            .andExpect(jsonPath("$.flights[0].fare").exists())
            .andExpect(jsonPath("$.flights[1].fare").exists())
        // TODO: Validation here
    }

    @Test
    fun `returns empty result when suppliers don't have any matching flight`() {
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
}
