# 프로젝트 컨벤션

이 문서는 **"코드만 봐서는 안 보이는 약속"** 을 정리한다.
왜 클래스를 이렇게 나눴는지, 무엇을 테스트하는지, 이름을 어떻게 짓는지.

기준 예시는 모든 레이어를 다 갖춘 `user` 도메인이다.

---

## 1. 변환 계층 컨벤션

### 1.1 데이터 흐름 한눈에

요청이 들어와서 도메인 모델이 되기까지, 변환은 **두 번** 일어난다.

```
HTTP Request
  │
  ▼  Request DTO        (adapter/in/web/request)
  │
  │  ── Mapper ──>       Request → Command/Query        [web 계층 변환]
  ▼
Command / Query          (application/dto/command, /query)
  │
  │  ── Assembler ──>    Command → Domain Model         [application 계층 변환]
  ▼
Domain Model             (model)
```

응답은 반대 방향으로, 도메인 모델 → Response DTO 로 변환된다.

### 1.2 Mapper vs Assembler — 헷갈리는 핵심

둘 다 "변환"이라 이름이 비슷하지만 **사는 위치와 변환 대상이 다르다.**

| 구분 | 위치 | 무엇을 → 무엇으로 | 예시 |
|------|------|------------------|------|
| **Mapper** | `adapter/in/web/mapper` | Request DTO → Command/Query | `CreateUserRequest` → `CreateUserCommand` |
| **Assembler** | `application/assembler` | Command/Query → Domain Model | `CreateUserCommand` → `User.create(...)` |

> **왜 나눴나?**
> Mapper는 "바깥세상(HTTP)"의 모양을 안쪽 언어(Command)로 번역하는 어댑터 계층의 책임이고,
> Assembler는 그 Command를 "진짜 도메인 객체"로 조립하는 application 계층의 책임이다.
> 웹이 GraphQL로 바뀌어도 Assembler는 그대로 쓸 수 있어야 한다 — 그게 분리의 목적.

**실제 코드**

```java
// Mapper: Request → Command  (adapter/in/web/mapper/UserCommandMapper.java)
public static BalanceChargeCommand toChargeCommand(String userId, BalanceChargeRequest request) {
    return BalanceChargeCommand.builder()
            .userId(userId)
            .balance(request.getAmount())
            .build();
}

// Assembler: Command → Model  (application/assembler/UserAssembler.java)
public static User toModel(CreateUserCommand command) {
    return User.create(command.getEmail(), command.getPassword());
}
```

### 1.3 Command vs Query — 읽기/쓰기 분리 (CQRS)

DTO도, Service도 **쓰기(Command)와 읽기(Query)를 분리**한다.

| 접미사 | 의미 | DTO 위치 | Service | Mapper |
|--------|------|----------|---------|--------|
| **Command** | 상태를 바꾸는 쓰기 요청 | `application/dto/command` | `UserCommandService` | `UserCommandMapper` |
| **Query** | 상태를 안 바꾸는 읽기 요청 | `application/dto/query` | `UserQueryService` | `UserQueryMapper` |

> **왜 나눴나?**
> 읽기와 쓰기는 트랜잭션 성격(`@Transactional` vs `readOnly = true`), 성능 최적화 방향,
> 동시성 제어 필요성이 전부 다르다. 한 서비스에 섞으면 책임이 비대해진다.
> 학습 목적상 CQRS의 입구를 경험하기 위한 분리이기도 하다.

- `Criteria` (예: `UserCriteria`)는 복잡한 검색 조건을 담는 별도 DTO로, Query보다 동적 조건이 많을 때 사용한다.

---

## 2. 테스트 전략

### 2.1 핵심 멘탈 모델

> **"이 코드에 `if` / 예외 / 계산 같은 *판단 로직*이 있나?"**
> → 있으면 테스트한다. 단순 데이터 운반이면 스킵한다.

클래스 이름이 아니라 **레이어의 책임**을 기준으로 테스트 대상을 고른다.

### 2.2 레이어별 우선순위

| 우선순위 | 대상 | 테스트 종류 | 무엇을 검증 |
|:---:|------|------------|------------|
| ★★★ | 도메인 모델 (`model`) | 순수 단위 테스트 | 비즈니스 규칙·예외 (잔액 부족, 만료, 좌석 상태 전이) |
| ★★★ | 서비스/유스케이스 (`application`) | Mock 단위 + 통합 | 흐름 조합, **동시성·일관성** (좌석 동시 예약, 잔액 동시 충전) |
| ★★ | 퍼시스턴스 어댑터 (`adapter/out`) | `@DataJpaTest` + Testcontainers | Entity↔Model 변환, 커스텀/락 쿼리 |
| ★ | 컨트롤러 (`adapter/in/web`) | `@WebMvcTest` | 요청 검증(`@Valid`), 매핑, 에러 응답 |

> 상세한 작성법(Given-When-Then, `@Nested`, `@DataJpaTest`, AssertJ 등)은
> [`TEST_GUIDE.md`](./TEST_GUIDE.md) 참조.

### 2.3 반드시 테스트할 것

- **도메인 모델의 분기/예외 케이스** — 가장 빠르고(ms) 가장 가치 있다.
  - `User.charge()` 음수 충전 방어, `User.use()` 잔액 부족 예외
  - `Reservation` 5분 만료 로직
  - `Seat.reserve()` AVAILABLE이 아닌 좌석 예약 차단
- **동시성** — 이 프로젝트의 핵심. 락(`ForUpdate`)이 실제로 동작하는지 통합 테스트로 검증.

### 2.4 테스트하지 말 것

- Getter / Setter, 단순 위임 메서드
- DTO / Mapper / Assembler의 1:1 필드 복사 (로직 없으면 스킵)
- 프레임워크가 보장하는 것 (JPA `save`가 저장되나? 같은 것)

---

## 3. 네이밍 컨벤션

### 3.1 클래스 접미사

| 역할 | 접미사 | 위치 | 예시 |
|------|--------|------|------|
| REST 컨트롤러 | `{Domain}Controller` | `adapter/in/web` | `UserController` |
| 도메인 서비스 (쓰기) | `{Domain}CommandService` | `application/service` | `UserCommandService` |
| 도메인 서비스 (읽기) | `{Domain}QueryService` | `application/service` | `UserQueryService` |
| 리포지토리 포트(인터페이스) | `{Domain}Repository` | `application` | `UserRepository` |
| 리포지토리 구현(어댑터) | `{Domain}RepositoryAdapter` | `adapter/out/persistence` | `UserRepositoryAdapter` |
| Spring Data JPA | `{Domain}JpaRepository` | `adapter/out/persistence` | `UserJpaRepository` |
| JPA 엔티티 | `{Domain}Entity` | `adapter/out/persistence` | `UserEntity` |
| 도메인 모델 | `{Domain}` | `model` | `User` |
| 요청→Command 변환 | `{Domain}CommandMapper` | `adapter/in/web/mapper` | `UserCommandMapper` |
| 요청→Query 변환 | `{Domain}QueryMapper` | `adapter/in/web/mapper` | `UserQueryMapper` |
| Command/Query→Model 변환 | `{Domain}Assembler` | `application/assembler` | `UserAssembler` |
| 에러 코드 | `{Domain}ErrorCode` | `exception` | `UserErrorCode` |

### 3.2 user 도메인 전체 구조 (레퍼런스)

```
domain/user/
├── adapter/
│   ├── in/web/
│   │   ├── UserController.java
│   │   ├── request/      CreateUserRequest, BalanceChargeRequest, FindUserRequest
│   │   ├── response/     UserResponse, BalanceResponse
│   │   └── mapper/       UserCommandMapper, UserQueryMapper
│   └── out/persistence/
│       ├── UserEntity.java
│       ├── UserJpaRepository.java
│       ├── UserRepositoryAdapter.java
│       └── UserSpecification.java
├── application/
│   ├── UserRepository.java          ← 포트(인터페이스)
│   ├── service/      UserCommandService, UserQueryService
│   ├── assembler/    UserAssembler
│   └── dto/
│       ├── command/  CreateUserCommand, BalanceChargeCommand
│       ├── query/    FindUserQuery
│       └── UserCriteria.java
├── model/            User, UserRole
└── exception/        UserErrorCode
```

### 3.3 메서드 네이밍

- Entity ↔ Model 변환: `Entity.toModel()`, `Entity.create(model)`
- 도메인 모델 생성: 정적 팩토리 `User.create(...)` (생성자 대신)
- 변환 메서드: `toModel`, `toCreateCommand`, `toChargeCommand`, `toFindQuery` 처럼 `to{대상}` 형태
- 테스트: `@DisplayName`에 한국어로 행위 서술, 메서드명은 `returnTrueWhen...` 같은 영어 서술형
