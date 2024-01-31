# Flight Search Application
This project implements a flight search application that aggregates flight data from multiple suppliers, such as CrazyAir and ToughJet. The application allows users to search for flights based on their travel requirements and provides a sorted list of available flights.

## API Endpoints
`POST /api/flights/search`
* Request Body: `FlightSearchRequestDTO`
* Response Body:` FlightSearchResponseDTO`

## Project Structure

### Model Classes
* `FlightSearchRequestDTO`, `FlightSearchResponseDTO`, `FlightDTO`: DTOs for interacting with the controller layer
* `FlightSearchRequest`, `FLight`: Plain object to interact with domain services. Independent of the API and suppliers contracts.

### Service Classes
* `FlightSearchService`: Orchestrates flight searches from different suppliers. Uses coroutines for parallel execution and allows for configurable timeouts. Provides a sorted list of flights.

### Supplier Classes
* `CrazyAirSupplier` and `ToughJetSupplier`: Implement the `FlightSearchSupplier` interface, performing flight searches for CrazyAir and ToughJet, respectively.

### Controller Class
* `FlightSearchController`: REST API controller that exposes an endpoint for searching flights. Uses `FlightSearchService` to retrieve flight data.

### Mapper Class
* `FlightSearchMapper`: Maps entities between the service layer and DTOs.

## Testing
* External clients (like `CrazyAirClient`) are tested with WireMock to ensure serialization works correctly
* Full integration tests of the controller are split between two classes. In `FlightSearchControllerErrorHandlingTest` validation and error responses are checked,
  while in `FlightSearchControllerTest` main functional tests are implemented. Those tests also use WireMock, to simulate a real environment as close as possible.
* In addition, supplier and service classes are covered with unit tests.

## Areas for improvement
As with any project, there are many possible improvements:
* **Money**: There is currently no support for multiple currencies in the app, and though the use of BigDecimal is sufficient for now, a more specific data type for handling money could be considered for better financial operations.
* **Validation**: The validation process could be unified by using a single approach instead of the mix of Kotlin checks and annotations that are currently used.
* **Versioning**: Depending on the exposed API usage and needs, we could consider API versioning. This could be useful if the API was available publicly.
* **Healthcheck**: The Actuator could be enabled to validate the app's health. Moreover, additional monitoring of the app and suppliers could be introduced.
* **Modularization && ArchUnit**: Splitting the project into modules could create better boundaries and ensure that different layer details are not leaking out. Similarly, introducing ArchUnit for tests could ensure high code quality.
* **Parallelization**: Since `RestTemplate` wasn't designed with async code execution in mind, we could consider using `WebClient` and Spring WebFlux for parallelization.
* **Resiliency**: We could introduce resiliency patterns for the app around suppliers to prevent app degradation from external issues:
  * retries if we fail to receive a response from a supplier
  * circuit breaker, if a given supplier starts failing
  * rate limiter, so we don't overload a supplier
  * caching to avoid unnecessary roundtrips for the same requests in a short time