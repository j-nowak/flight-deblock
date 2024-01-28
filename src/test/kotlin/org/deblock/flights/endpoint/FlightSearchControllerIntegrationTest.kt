package org.deblock.flights.endpoint

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class FlightSearchControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

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
            post("/api/flights")
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
            post("/api/flights")
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
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.flights").isEmpty)
    }

    @Test
    fun `returns 400 Bad Request when departure date is later than return date`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-01-30",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `accepts same day flight requests`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-01",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `returns 400 Bad Request when number of passengers is too big`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 5
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error_message").value("Number of passengers cannot exceed 4"))
    }

    @Test
    fun `returns 400 Bad Request when origin is missing`() {
        val requestJson =
            """
            {
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when destination is missing`() {
        val requestJson =
            """
            {
                "origin": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when origin is an invalid code`() {
        val requestJson =
            """
            {
                "origin": "INVALID",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when destination is an invalid code`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "INVALID",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when origin and destination are the same place`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "LHR",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when numberOfPassengers is 0`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 0
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when departureDate is invalid`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "INVALID_DATE",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when departureDate is in wrong format`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024.02.01",
                "returnDate": "2024-02-10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when returnDate is invalid`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "INVALID_DATE",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns 400 Bad Request when returnDate is in wrong format`() {
        val requestJson =
            """
            {
                "origin": "LHR",
                "destination": "AMS",
                "departureDate": "2024-02-01",
                "returnDate": "2024.02.10",
                "numberOfPassengers": 2
            }
            """.trimIndent()

        mockMvc.perform(
            post("/api/flights")
                .content(requestJson)
                .contentType("application/json"),
        )
            .andExpect(status().isBadRequest)
    }
}
