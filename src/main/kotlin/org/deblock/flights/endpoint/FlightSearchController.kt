package org.deblock.flights.endpoint

import jakarta.validation.Valid
import org.deblock.flights.dto.FlightSearchRequest
import org.deblock.flights.dto.FlightSearchResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/flights")
@Validated
class FlightSearchController {
    @PostMapping
    fun search(
        @Valid @RequestBody flightSearchRequest: FlightSearchRequest,
    ): FlightSearchResponse {
        return FlightSearchResponse(emptyList())
    }
}
