package org.deblock.flights.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.deblock.flights.service.supplier.FlightSearchSupplier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.LocalDate
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@ExperimentalCoroutinesApi
class FlightSearchServiceTest {

    @Test
    fun `searchFlights should return aggregated and sorted flights`() = runTest {
        // Given
        val supplier1 = mockSupplier(
            listOf(flight("A", 200.0, "Supplier1"), flight("B", 150.0, "Supplier1"))
        )
        val supplier2 = mockSupplier(
            listOf(flight("C", 180.0, "Supplier2"), flight("D", 220.0, "Supplier2"))
        )

        val service = FlightSearchService(listOf(supplier1, supplier2), 5.toDuration(DurationUnit.SECONDS))

        // When
        val result = service.searchFlights(searchRequest())

        // Then
        assertEquals(4, result.size)
        assertEquals("B", result[0].airline)
        assertEquals(150.0, result[0].fare)
        assertEquals("C", result[1].airline)
        assertEquals(180.0, result[1].fare)
        assertEquals("A", result[2].airline)
        assertEquals(200.0, result[2].fare)
        assertEquals("D", result[3].airline)
        assertEquals(220.0, result[3].fare)
    }

    @Test
    fun `searchFlights should return an empty list when there are no results from one supplier`() = runTest {
        // Given
        val supplier1 = mockSupplier(emptyList())
        val supplier2 = mockSupplier(listOf(
            flight(airline = "TestAir", fare = 100.0)
        ))

        val service = FlightSearchService(
            listOf(supplier1, supplier2),
            supplierTimeout = 5.toDuration(DurationUnit.SECONDS)
        )

        // When
        val result = service.searchFlights(searchRequest())

        // Then
        assertEquals(1, result.size)
        assertEquals("TestAir", result[0].airline)
        assertEquals(100.0, result[0].fare)
    }

    @Test
    fun `searchFlights should return an empty list when both suppliers return nothing`() = runTest {
        // Given
        val supplier1 = mockSupplier(emptyList())
        val supplier2 = mockSupplier(emptyList())

        val service = FlightSearchService(
            listOf(supplier1, supplier2),
            supplierTimeout = 5.toDuration(DurationUnit.SECONDS)
        )

        // When
        val result = service.searchFlights(searchRequest())

        // Then
        assertTrue(result.isEmpty())
    }

    private fun mockSupplier(flights: List<Flight>): FlightSearchSupplier {
        val mockSupplier = mock<FlightSearchSupplier>()
        whenever(mockSupplier.searchFlights(any())).thenReturn(flights)
        return mockSupplier
    }

    private fun flight(airline: String, fare: Double, supplier: String = "MockSupplier"): Flight {
        return Flight(
            airline = airline,
            supplier = supplier,
            fare = fare,
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = Instant.parse("2022-01-01T10:00:00Z"),
            arrivalDate = Instant.parse("2022-01-01T12:00:00Z")
        )
    }

    private fun searchRequest(): FlightSearchRequest {
        return FlightSearchRequest(
            origin = "LHR",
            destination = "AMS",
            departureDate = LocalDate.parse("2022-01-01"),
            returnDate = LocalDate.parse("2022-01-10"),
            numberOfPassengers = 2
        )
    }
}