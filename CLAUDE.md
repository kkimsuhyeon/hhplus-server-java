# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HangHae Plus Concert Reservation System - A Spring Boot 3.4.1 application for managing concert reservations, payments, and user queuing.

**Tech Stack:**
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- MySQL 8.0 (local) / H2 (testing)
- Redis
- Testcontainers
- Gradle (Kotlin DSL)
- Lombok

## Development Commands

```bash
# Build
./gradlew build
./gradlew clean build

# Run tests
./gradlew test
./gradlew test --tests "*ConcertServiceTest"                    # Pattern match
./gradlew test --tests "kr.hhplus.be.server.domain.concert.model.ConcertTest"  # Specific class

# Run application (requires docker-compose up -d first)
./gradlew bootRun

# Docker infrastructure
docker-compose up -d   # Start MySQL
docker-compose down    # Stop containers
```

## Architecture

### Hexagonal Architecture (Ports & Adapters)

```
src/main/java/kr/hhplus/be/server/
├── application/              # Cross-domain use cases
│   ├── dto/                  # Commands shared across domains
│   └── usecase/              # PaymentUseCase, ReservationUseCase
├── config/                   # Global configuration
│   └── exception/            # BusinessException, ErrorCode interface
├── domain/{domain}/          # Each domain follows this structure:
│   ├── adapter/
│   │   ├── in/web/           # Controllers, Request/Response DTOs, Mappers
│   │   └── out/persistence/  # JPA Entities, Repositories, Adapters
│   ├── application/
│   │   ├── dto/              # Commands, Queries, Criteria
│   │   ├── repository/       # Repository interfaces (ports)
│   │   └── service/          # Domain services
│   ├── exception/            # Domain-specific error codes
│   └── model/                # Domain models (pure business logic)
└── shared/                   # Shared utilities
```

**Domains:** `concert`, `reservation`, `payment`, `user`, `token`

### Key Patterns

**Domain Model vs JPA Entity Separation:**
- Domain models in `model/` are immutable with `@Builder`
- JPA entities in `adapter/out/persistence/entity/` handle persistence
- Conversion: `Entity.toModel()` and `Entity.create(model)`

**Repository Pattern:**
- Interface in `application/repository/` (port)
- Implementation in `adapter/out/persistence/adapter/` (adapter)
- Returns domain models, not entities

**Data Flow:**
```
Controller → UseCase/Service → Repository Interface → RepositoryAdapter → JPA Entity → DB
```

### Exception Handling

Domain-specific error codes implement `ErrorCode` interface:
```java
public enum ConcertErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "CCT001", "콘서트를 찾을 수 없습니다");
}
```

Throw `BusinessException` with error codes:
```java
throw new BusinessException(ConcertErrorCode.NOT_FOUND);
```

## Configuration

**Profiles:**
- `local` - H2 in-memory (development)
- `test` - H2 in-memory with `create-drop`

**JPA:**
- `open-in-view: false`
- UTC timezone normalization
- HikariCP max 3 connections

## Testing

**Test Types:**
- Unit tests: No Spring context, test domain logic
- Integration tests: `@DataJpaTest` for repositories, `@SpringBootTest` for services
- Testcontainers for MySQL integration

**Patterns:**
- Given-When-Then structure
- `@Nested` for grouping related tests
- AssertJ for assertions
- System property: `user.timezone=UTC`

## Domain Details

### Concert Flow
- Concert 1:N ConcertSchedule 1:N Seat
- Seat status: `AVAILABLE` → `RESERVING` → `RESERVED`

### Reservation Flow
1. User reserves seat (status → `RESERVING`, 5-min expiry)
2. Payment completes (seat → `RESERVED`)
3. Expired reservations release seats

### Payment
- Deducts from user balance
- Uses optimistic locking with `@Retryable` for concurrency

### Token/Queue
- Queue management for high-traffic scenarios
- States: `WAITING` → `ACTIVE` → `EXPIRED`

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Controller | `{Domain}Controller` | `ConcertController` |
| Service | `{Domain}Service` | `ConcertService` |
| Repository Interface | `{Domain}Repository` | `ConcertRepository` |
| Repository Impl | `{Domain}RepositoryAdapter` | `ConcertRepositoryAdapter` |
| JPA Repository | `{Domain}JpaRepository` | `ConcertJpaRepository` |
| JPA Entity | `{Domain}Entity` | `ConcertEntity` |
| Domain Model | `{Domain}` | `Concert` |
