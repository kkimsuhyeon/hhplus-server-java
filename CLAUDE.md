# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HangHae Plus Concert Reservation System - A Spring Boot 3.4.1 application for managing concert reservations, payments, and user queuing.

**Tech Stack:**
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- MySQL 8.0 (production/local)
- H2 (testing)
- Testcontainers
- Gradle (Kotlin DSL)
- Lombok

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Run application (requires Docker containers running)
./gradlew bootRun
```

### Docker Infrastructure
```bash
# Start MySQL container (required for local profile)
docker-compose up -d

# Stop containers
docker-compose down
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "kr.hhplus.be.server.domain.concert.model.entity.ConcertEntityTest"

# Run tests with pattern
./gradlew test --tests "*ConcertServiceTest"
```

**Test Configuration:**
- JUnit 5 Platform
- Mockito with Java Agent
- System property: `user.timezone=UTC`
- Test profiles use H2 in-memory database
- Integration tests use `@DataJpaTest` with Testcontainers

## Architecture

### Hexagonal Architecture Pattern

The codebase follows a **modified Hexagonal Architecture** with clear separation between domain logic and infrastructure:

```
domain/
├── {domain-name}/
│   ├── adapter/
│   │   ├── in/web/          # REST Controllers, Request/Response DTOs, Mappers
│   │   └── out/persistence/ # JPA Entities, Repositories, Adapters
│   ├── application/
│   │   ├── dto/             # Commands, Queries, Criteria
│   │   ├── repository/      # Domain Repository Interfaces (ports)
│   │   └── service/         # Domain Services
│   ├── model/               # Domain Models (pure business logic)
│   └── exception/           # Domain-specific exceptions
```

**Key Domains:**
- `concert` - Concert, Schedule, and Seat management
- `reservation` - Reservation lifecycle
- `payment` - Payment processing
- `user` - User balance and management
- `token` - Queue token management

### Layer Responsibilities

**1. Domain Model (`model/`):**
- Pure business objects with no framework dependencies
- Immutable with `@Builder` and `@AllArgsConstructor`
- Contains business validation logic
- Example: `Concert.java`, `Reservation.java`

**2. Domain Services (`application/service/`):**
- Orchestrates domain logic
- Uses Repository interfaces (ports)
- Transaction boundaries with `@Transactional`
- Example: `ConcertService.java`

**3. Repository Interfaces (`application/repository/`):**
- Define data access contracts (outbound ports)
- Domain-agnostic, return domain models
- Example: `ConcertRepository.java`

**4. Adapter Layer:**
- **In Adapters (`adapter/in/web/`)**: REST Controllers, mapping web requests to domain
- **Out Adapters (`adapter/out/persistence/`)**: JPA implementation of repositories
  - `entity/` - JPA Entities (separate from domain models)
  - `repository/` - Spring Data JPA repositories
  - `adapter/` - Repository implementation adapting JPA to domain

**5. Use Cases (`application/usecase/`):**
- Cross-domain orchestration
- Example: `PaymentUseCase.java` coordinates Reservation and User services

### Data Flow

```
HTTP Request
  → Controller (adapter/in/web)
  → UseCase or Service (application)
  → Repository Interface (application/repository)
  → Repository Adapter (adapter/out/persistence/adapter)
  → JPA Entity & Repository (adapter/out/persistence)
  → Database
```

**Key Pattern:**
- Controllers use mappers to convert Request DTOs → Commands/Queries
- Services work with domain models
- Repository adapters convert between JPA Entities ↔ Domain Models
- Responses are mapped from domain models to Response DTOs

## Domain Details

### Concert Domain
- **Concert**: Top-level entity with title and description
- **ConcertSchedule**: Specific concert date/time instances
- **Seat**: Individual seats with status (AVAILABLE, RESERVING, RESERVED)

### Reservation Flow
1. User creates reservation (seat status → RESERVING)
2. Reservation has 5-minute expiry (`expired_at`)
3. Payment completes reservation (seat status → RESERVED)

### Payment & User Balance
- Users have balance that can be charged
- Payment deducts from user balance
- Payment history tracked separately

### Token/Queue System
- Manages waiting queue for high-traffic concert reservations
- Token states: WAITING, ACTIVE, EXPIRED

## Configuration & Profiles

**Application Profiles:**
- `local` - Uses MySQL via Docker (requires `docker-compose up`)
- `dev` - Development environment
- `test` - H2 in-memory database

**JPA Configuration:**
- `open-in-view: false` (explicit transaction management)
- UTC timezone normalization
- HikariCP connection pool (max 3 connections)

## Exception Handling

**Exception Hierarchy:**
```
BusinessException (extends RuntimeException)
  └── Domain-specific error codes (e.g., ConcertErrorCode.NOT_FOUND)

ServerErrorException
ValidationError
```

**Error Codes:**
- Centralized in domain-specific enums (e.g., `ConcertErrorCode`)
- Implement `ErrorCode` interface
- Handled globally by `GlobalExceptionHandler`

## Testing Strategy

### Test Types

**1. Unit Tests (Entity/Model tests):**
- Fast, no Spring context
- Test domain logic, builders, business methods
- Example: `ConcertEntityTest.java`

**2. Integration Tests (Service/Repository):**
- Use `@DataJpaTest` for repository tests
- Use `@SpringBootTest` or mocks for service tests
- Testcontainers for database integration
- Example: `ConcertRepositoryAdapterTest.java`

### Test Patterns

**Given-When-Then Structure:**
```java
@Test
void shouldDoSomething() {
    // given: setup test data
    // when: execute the test
    // then: verify results
}
```

**Use `@Nested` for grouping:**
```java
@Nested
@DisplayName("Entity Creation Tests")
class CreateEntityTest {
    // related tests
}
```

**Reference:** See `docs/TEST_GUIDE.md` for detailed testing guidance.

## Code Style & Conventions

### Entity Design
- Domain Models: Immutable with `@Builder`, no setters
- JPA Entities: Separate from domain models, in `adapter/out/persistence/entity/`
- Conversion methods: `Entity.toModel()` and `Entity.create(model)`

### Service Patterns
- Use `@Transactional(readOnly = true)` for read operations
- Use `@Transactional` for write operations
- Throw `BusinessException` with domain error codes

### Naming Conventions
- Controllers: `{Domain}Controller`
- Services: `{Domain}Service`
- Repositories: `{Domain}Repository` (interface), `{Domain}RepositoryAdapter` (implementation)
- JPA Repos: `{Domain}JpaRepository`
- Entities: `{Domain}Entity` (JPA), `{Domain}` (domain model)

## API Documentation

- Swagger/OpenAPI enabled via SpringDoc
- API specification available in `API_SPECIFICATION.md`
- Swagger UI available at `/swagger-ui.html` when running

## Important Notes

### Concurrency Handling
- The system requires concurrency control for seat reservations
- Seat status transitions must be atomic
- Payment and reservation must maintain consistency

### Time Handling
- All timestamps use UTC
- System timezone set to UTC in tests
- JPA timezone normalization enabled

### Version Management
- Project version derived from git commit hash
- See `build.gradle.kts`: `version = getGitHash()`

## Database Schema

See `ERD_Concert_Reservation_System.md` for full entity relationship diagram.

**Key Relationships:**
- Concert 1:N ConcertSchedule
- ConcertSchedule 1:N Seat
- User 1:N Reservation
- Reservation 1:1 Payment
- Seat temporary assignment to User during reservation
