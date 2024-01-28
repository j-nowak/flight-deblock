package org.deblock.flights.controller

import jakarta.validation.Valid
import org.deblock.flights.controller.mapper.FlightSearchMapper
import org.deblock.flights.controller.dto.FlightSearchRequestDTO
import org.deblock.flights.controller.dto.FlightSearchResponseDTO
import org.deblock.flights.controller.dto.toSearchRequest
import org.deblock.flights.service.FlightSearchService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/flights")
@Validated
class FlightSearchController(
    private val flightSearchService: FlightSearchService,
) {
    @PostMapping("/search")
    fun search(
        @Valid @RequestBody flightSearchRequestDTO: FlightSearchRequestDTO,
    ): FlightSearchResponseDTO {
        val searchResult = flightSearchService.searchFlights(flightSearchRequestDTO.toSearchRequest())
        return FlightSearchMapper.map(searchResult)
    }
}
