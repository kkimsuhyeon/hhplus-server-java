# 테스트 가이드

**프로젝트**: 항해플러스 콘서트 예약 시스템
**스택**: JUnit 5 · AssertJ · Mockito · Spring Boot Test · `@DataJpaTest` + Testcontainers
**원칙**: Given-When-Then, `@Nested` 그룹화, `@DisplayName`로 의도 표현, 필요한 최소 범위만 부팅

---

## 목차

1. [테스트 종류와 선택 기준](#1-테스트-종류와-선택-기준)
2. [공통 컨벤션](#2-공통-컨벤션)
3. [단위 테스트 (도메인 모델 / 엔티티)](#3-단위-테스트-도메인-모델--엔티티)
4. [JPA 통합 테스트 (`@DataJpaTest`)](#4-jpa-통합-테스트-datajpatest)
5. [서비스 / 유스케이스 테스트](#5-서비스--유스케이스-테스트)
6. [AssertJ 레퍼런스](#6-assertj-레퍼런스)
7. [Best Practices](#7-best-practices)
8. [체크리스트](#8-체크리스트)

---

## 1. 테스트 종류와 선택 기준

### 1.1 어떤 테스트 종류를 고를까

| 종류 | 부팅 | 속도 | 언제 쓰나 |
|---|---|---|---|
| **단위 테스트** | 없음 | 매우 빠름 | 도메인 모델 비즈니스 로직(`Seat.reserve()`, `User.deductBalance()` 등), 순수 계산/검증, 정적 팩토리 |
| **JPA 통합 테스트** (`@DataJpaTest`) | JPA 슬라이스 + H2 또는 Testcontainers | 보통 | 엔티티 매핑, JPA 쿼리(`@Query`), Auditing, 제약조건, 락(`PESSIMISTIC_WRITE`) 동작 |
| **서비스 테스트** | 없음 (Mockito) | 빠름 | Repository 인터페이스를 mock해서 service의 분기/예외/트랜잭션 의도 검증 |
| **유스케이스 통합 테스트** | `@SpringBootTest` | 느림 | 교차 도메인 흐름(예약→결제), 트랜잭션 전파, 동시성 검증 |
| **동시성 테스트** | `@SpringBootTest` + Testcontainers (MySQL) | 가장 느림 | `ExecutorService` + `CountDownLatch` 기반 race condition 재현 |

> 단위 테스트로 충분한 것을 통합 테스트로 쓰지 말 것. 통합 테스트로만 검증 가능한 것을 단위 테스트로 흉내 내지도 말 것.

### 1.2 테스트할까 말까 — 단 하나의 질문

새 코드를 보면 딱 이것만 물어본다:

> **"이 메서드 안에 `if` / 예외 던지기 / 계산 / 상태 변경이 있나?"**
> - **있다 → 테스트한다.** 그 판단이 틀리면 곧 버그다.
> - **없다 (값을 그냥 옮기기만 함) → 테스트하지 않는다.** 검증할 게 없다.

**테스트하는 것 (이 프로젝트 실제 예시)**

```java
// Seat.reserve() — 한 메서드 안에 판단 + 예외 + 상태 변경이 다 있다 → 1순위 테스트 대상
public void reserve(String userId) {
    if (!isReservable()) {                                          // 분기
        throw new BusinessException(SeatErrorCode.ALREADY_RESERVED); // 예외
    }
    this.status = SeatStatus.RESERVING;                            // 상태 변경
    this.userId = userId;
    this.holdExpiresAt = LocalDateTime.now().plusMinutes(5);
}
```

`User.addBalance()`(음수 방어), `User.deductBalance()`(잔액 부족), `Seat.confirm()`(상태 검증)도 같은 이유로 전부 테스트한다.

**테스트하지 않는 것**

| 안 하는 것 | 이 프로젝트 예시 | 이유 |
|-----------|----------------|------|
| 단순 Getter/Setter | Lombok `@Getter` | 내가 짠 로직이 아님 |
| 1:1 값 복사 변환 | `UserAssembler.toModel()`, `UserCommandMapper` | 판단·계산이 없음 |
| 프레임워크가 보장하는 것 | `JpaRepository.save()` 자체 | 스프링/하이버네이트가 이미 검증함 |
| 단순 위임 | 다른 객체를 호출만 하는 메서드 | 검증할 분기가 없음 |

> 단, `Assembler`/`Mapper`라도 **안에 조건 분기나 계산이 생기면** 그 순간부터 테스트 대상이다.
> 기준은 클래스 "이름"이 아니라 "안에 판단 로직이 있나"다.

### 1.3 한 메서드에 케이스를 몇 개 짜야 하나?

**경로(분기) 하나당 테스트 하나.** 빠뜨리지 않는 공식:

> **성공 경로 1개 + `if`/예외마다 1개씩** (+ 여유 되면 경계값 1개)

`User.deductBalance()` 예시:

```java
public void deductBalance(BigDecimal amount) {
    if (this.balance.compareTo(amount) < 0) {     // 경로 A: 잔액 부족
        throw new BusinessException(UserErrorCode.NOT_ENOUGH_POINT);
    }
    this.balance = this.balance.subtract(amount); // 경로 B: 정상 차감
}
```

경로가 2개 → 테스트도 2개 (실제 `UserCommandServiceTest`에 이대로 있다):
- ✅ `deductBalance_Success` — 잔액 충분 → 잔액이 줄어드는가
- ✅ `deductBalance_Fail` — 잔액 부족 → `NOT_ENOUGH_POINT` 예외가 나는가
- (＋) 경계값: 잔액과 **딱 같은 금액** 차감 → 0원으로 성공하는가

**then(검증)에 뭘 적을지 모르겠다면** → "이 메서드가 바꾼 것을 전부" 적는다.
`reserve()`는 status·userId·holdExpiresAt 3개를 바꾸므로 → 3개 모두 검증 (2.1 예시 참고).

### 1.4 막힐 때 보는 결정 순서

새 클래스를 만들었다 → 위에서부터 따라간다:

1. 안에 `if`/예외/계산/상태변경이 있나? → 없으면 **스킵**, 있으면 계속
2. 의존성(Repository 등)이 있나? → 없으면 **모델 단위 테스트**(3장), 있으면 **`@Mock` 서비스 테스트**(5장)
3. 메서드의 경로를 센다 → **성공 1개 + 분기/예외마다 1개** (1.3)
4. 각 테스트는 given(상태) → when(동작) → then(바뀐 것 전부 검증) (2.1)
5. 잔액·좌석처럼 동시성이 중요한 흐름이면 → 동시성 통합 테스트 추가 (5.3)

---

## 2. 공통 컨벤션

### 2.1 Given-When-Then

```java
@Test
@DisplayName("AVAILABLE 좌석을 예약하면 RESERVING으로 바뀐다")
void reserveAvailableSeat() {
    // given
    Seat seat = Seat.create(BigDecimal.valueOf(10_000), "schedule-1");

    // when
    seat.reserve("user-1");

    // then
    assertThat(seat.getStatus()).isEqualTo(SeatStatus.RESERVING);
    assertThat(seat.getUserId()).isEqualTo("user-1");
    assertThat(seat.getHoldExpiresAt()).isAfter(LocalDateTime.now());
}
```

### 2.2 `@Nested` + `@DisplayName`

```java
@Nested
@DisplayName("reserve()는")
class ReserveTest {

    @Test
    @DisplayName("AVAILABLE 상태에서만 예약 가능하다")
    void reserveAvailable() { ... }

    @Test
    @DisplayName("이미 예약된 좌석이면 ALREADY_RESERVED 예외를 던진다")
    void rejectAlreadyReserved() { ... }
}
```

### 2.3 명명

- 클래스: `{대상}Test` (예: `SeatTest`, `SeatServiceTest`, `SeatRepositoryAdapterTest`)
- 메서드: `{기대 결과}When{조건}` 또는 자유 영어 + 한글 `@DisplayName`
- 한 테스트 = 한 동작. assertion이 늘어나면 의도가 흐려진다.

### 2.4 시간 처리

`LocalDateTime.now()` 직접 사용을 피한다 (도메인 모델에 `LocalDateTime.now()` 박혀 있는 곳이 있는데 이건 리팩토링 대상 — `CODE_REVIEW_REPORT.md` M-OLD-14 참고). 가능하면 시간을 파라미터/`Clock`으로 주입받아 테스트에서 고정값 전달.

### 2.5 테스트 프로파일

- 단위/JPA 슬라이스: `@ActiveProfiles("test")` (H2 + `src/test/resources/application.yml`)
- 통합/동시성: Testcontainers MySQL (실제 락/제약 검증 필요한 경우)

---

## 3. 단위 테스트 (도메인 모델 / 엔티티)

스프링/JPA 부팅 없이 객체 그 자체만 검증.

### 3.1 정적 팩토리 / 빌더

```java
@Test
@DisplayName("Seat.create()는 AVAILABLE 상태로 시작한다")
void createSeatStartsAsAvailable() {
    Seat seat = Seat.create(BigDecimal.valueOf(10_000), "schedule-1");

    assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    assertThat(seat.getUserId()).isNull();
    assertThat(seat.getHoldExpiresAt()).isNull();
}
```

### 3.2 비즈니스 메서드 (상태 전이)

`@Nested`로 메서드별로 묶는다.

```java
@Nested
@DisplayName("confirm()은")
class ConfirmTest {

    @Test
    @DisplayName("RESERVING이 아닌 좌석은 INVALID_STATUS 예외를 던진다")
    void rejectNonReservingSeat() {
        Seat seat = Seat.create(BigDecimal.TEN, "schedule-1");
        // status는 AVAILABLE

        assertThatThrownBy(seat::confirm)
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(SeatErrorCode.INVALID_STATUS);
    }
}
```

### 3.3 헬퍼

반복되는 객체 생성은 private static 메서드로 추출.

```java
private static Seat reservingSeat(String userId) {
    Seat seat = Seat.create(BigDecimal.TEN, "schedule-1");
    seat.reserve(userId);
    return seat;
}
```

---

## 4. JPA 통합 테스트 (`@DataJpaTest`)

JPA 슬라이스만 부팅. 각 테스트는 자동 롤백.

### 4.1 기본 골격

```java
@DataJpaTest
@ActiveProfiles("test")
@Import(SeatRepositoryAdapter.class)   // 어댑터를 같이 빈으로 등록
class SeatRepositoryAdapterTest {

    @Autowired private TestEntityManager em;
    @Autowired private SeatRepositoryAdapter adapter;

    @Test
    @DisplayName("findByIdWithLock은 PESSIMISTIC_WRITE 락을 잡는다")
    void findByIdWithLock() {
        ConcertScheduleEntity schedule = em.persistAndFlush(scheduleFixture());
        Seat seat = Seat.create(BigDecimal.TEN, schedule.getId());
        Seat saved = adapter.save(seat);

        em.clear();   // 1차 캐시 비워 실제 SELECT 강제

        Optional<Seat> locked = adapter.findByIdWithLock(saved.getId());

        assertThat(locked).isPresent();
    }
}
```

### 4.2 `em.clear()`를 잊지 말 것

1차 캐시에서 가져오면 실제 SQL이 안 나간다.

```java
em.persistAndFlush(entity);
em.clear();   // ← 이거 없으면 DB 다녀온 게 아님

Foo found = em.find(Foo.class, id);   // 이제 진짜 SELECT
```

### 4.3 제약조건 위반 검증

```java
@Test
@DisplayName("(user_id, seat_id) UNIQUE 위반 시 예외")
void uniqueConstraintViolation() {
    ReservationEntity first = ReservationEntity.create(reservationFixture("user-1", "seat-1"));
    em.persistAndFlush(first);

    ReservationEntity duplicate = ReservationEntity.create(reservationFixture("user-1", "seat-1"));

    assertThatThrownBy(() -> em.persistAndFlush(duplicate))
        .isInstanceOf(PersistenceException.class);
}
```

### 4.4 Auditing 검증

영속화 전에는 `@CreatedDate`가 null이다.

```java
ReservationEntity entity = ReservationEntity.create(model);
assertThat(entity.getCreatedAt()).isNull();   // 아직 null

ReservationEntity saved = em.persistAndFlush(entity);
assertThat(saved.getCreatedAt()).isNotNull(); // 이제 채워짐
```

### 4.5 Testcontainers (실DB 필요할 때)

H2로는 못 잡는 MySQL 특화 동작(예: `FOR UPDATE` 정확한 시맨틱, 인덱스 동작)이 필요하면 Testcontainers.

```java
@SpringBootTest
@Testcontainers
class ReservationUseCaseConcurrencyTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);
    }
}
```

---

## 5. 서비스 / 유스케이스 테스트

### 5.1 서비스 (Mockito)

도메인 Repository **인터페이스**를 mock해서 분기/예외만 본다. 어댑터 구현은 4번에서 따로.

```java
@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock private SeatRepository repository;
    @InjectMocks private SeatService service;

    @Test
    @DisplayName("getSeat: 좌석이 없으면 NOT_FOUND")
    void notFound() {
        given(repository.findById("x")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSeat("x"))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(SeatErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("reserve: 락으로 가져온 좌석에 reserve()를 호출하고 저장한다")
    void reserveDelegatesToDomain() {
        Seat seat = Seat.create(BigDecimal.TEN, "schedule-1");
        given(repository.findByIdWithLock("seat-1")).willReturn(Optional.of(seat));
        given(repository.update(any())).willAnswer(inv -> inv.getArgument(0));

        Seat result = service.reserve("seat-1", "user-1");

        assertThat(result.getStatus()).isEqualTo(SeatStatus.RESERVING);
        then(repository).should().findByIdWithLock("seat-1");
        then(repository).should().update(any(Seat.class));
    }
}
```

### 5.2 유스케이스 통합 테스트

도메인 간 흐름은 mocking보다 실 빈으로 검증한다 (mocking이 누락한 트랜잭션/예외 흐름이 자주 있음).

```java
@SpringBootTest
@ActiveProfiles("test")
class ReservationUseCaseIntegrationTest {

    @Autowired private ReservationUseCase useCase;
    @Autowired private TestEntityManager em;

    @Test
    @DisplayName("이미 PENDING_PAYMENT 예약이 있으면 ALREADY_HAVE_RESERVATION")
    void rejectIfUserHasPendingReservation() {
        // given: 유저 + 진행 중 예약
        // ...

        // when & then
        assertThatThrownBy(() -> useCase.reserve(command))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(ReservationErrorCode.ALREADY_HAVE_RESERVATION);
    }
}
```

### 5.3 동시성 테스트

`ExecutorService` + `CountDownLatch`로 race를 강제한다.

```java
@Test
@DisplayName("같은 좌석을 N명이 동시에 예약 시도하면 1명만 성공한다")
void concurrentReserve() throws InterruptedException {
    int threads = 10;
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threads);
    AtomicInteger success = new AtomicInteger();
    AtomicInteger fail = new AtomicInteger();

    try (ExecutorService es = Executors.newFixedThreadPool(threads)) {
        for (int i = 0; i < threads; i++) {
            String userId = "user-" + i;
            es.submit(() -> {
                try {
                    start.await();
                    useCase.reserve(new ReserveSeatCommand(userId, "seat-1"));
                    success.incrementAndGet();
                } catch (BusinessException e) {
                    fail.incrementAndGet();
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        done.await(5, TimeUnit.SECONDS);
    }

    assertThat(success.get()).isEqualTo(1);
    assertThat(fail.get()).isEqualTo(threads - 1);
}
```

---

## 6. AssertJ 레퍼런스

`import static org.assertj.core.api.Assertions.*;`

### 6.1 기본

```java
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotNull();
assertThat(actual).isNull();
assertThat(actual).isSameAs(expected);          // 참조 동일
```

### 6.2 문자열

```java
assertThat(text)
    .isNotBlank()
    .hasSize(11)
    .startsWith("Hello").endsWith("World")
    .contains("lo Wo")
    .doesNotContain("Goodbye")
    .matches("Hello.*")
    .isEqualToIgnoringCase("hello world");
```

### 6.3 숫자 / 시간

```java
assertThat(n).isPositive().isBetween(5, 15);
assertThat(price).isCloseTo(150_000L, within(100L));

assertThat(future)
    .isAfter(now)
    .isBefore(now.plusMonths(1))
    .isBetween(now, now.plusYears(1));
```

### 6.4 컬렉션

```java
assertThat(list)
    .hasSize(3)
    .contains("A", "B")                          // 포함 (순서 무관)
    .containsOnly("A", "B", "C")                 // 정확히 이 요소들만
    .containsExactly("A", "B", "C")              // 순서까지
    .containsExactlyInAnyOrder("C", "B", "A")    // 순서 무관, 중복 허용 X
    .doesNotContain("D")
    .doesNotContainNull()
    .allMatch(s -> s.length() == 1)
    .anyMatch(s -> s.equals("A"))
    .noneMatch(s -> s.equals("D"));
```

### 6.5 Enum

```java
assertThat(status)
    .isEqualTo(SeatStatus.AVAILABLE)
    .isIn(SeatStatus.AVAILABLE, SeatStatus.RESERVING)
    .isNotIn(SeatStatus.RESERVED);
```

### 6.6 필드 추출 / 필터링

```java
assertThat(users)
    .extracting(User::getName, User::getAge)
    .containsExactly(
        tuple("홍길동", 20),
        tuple("김철수", 25)
    );

assertThat(seats)
    .filteredOn(s -> s.getStatus() == SeatStatus.AVAILABLE)
    .extracting(Seat::getId)
    .containsExactly("seat-1", "seat-2");
```

### 6.7 객체 내부 다중 검증

```java
assertThat(concert).satisfies(c -> {
    assertThat(c.getTitle()).isNotEmpty();
    assertThat(c.getDescription()).isNotEmpty();
    assertThat(c.getCreatedAt()).isNotNull();
});

assertThat(concerts).allSatisfy(c -> assertThat(c.getId()).isNotNull());
assertThat(concerts).anySatisfy(c -> assertThat(c.getTitle()).contains("BTS"));
assertThat(concerts).noneSatisfy(c -> assertThat(c.getTitle()).isEmpty());
```

### 6.8 예외

```java
// 일반
assertThatThrownBy(() -> service.getConcert(null))
    .isInstanceOf(BusinessException.class)
    .hasMessageContaining("필수");

// 도메인 BusinessException + ErrorCode
assertThatThrownBy(() -> seat.reserve("u1"))
    .isInstanceOf(BusinessException.class)
    .extracting("errorCode")
    .isEqualTo(SeatErrorCode.ALREADY_RESERVED);

// 예외 안 나는지
assertThatCode(() -> service.getConcert("valid"))
    .doesNotThrowAnyException();
```

### 6.9 Soft Assertions (한 테스트에 검증 여러 개 묶기)

```java
@Test
void softly() {
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(concert.getId()).isNotNull();
    softly.assertThat(concert.getTitle()).isEqualTo("BTS 콘서트");
    softly.assertThat(concert.getDescription()).isNotEmpty();
    softly.assertAll();
}

// 또는 JUnit 5 extension
@ExtendWith(SoftAssertionsExtension.class)
class MyTest {
    @Test
    void test(SoftAssertions softly) {
        softly.assertThat(actual).isEqualTo(expected);  // assertAll() 자동
    }
}
```

### 6.10 Custom Assertion (도메인 특화)

자주 검증하는 객체에는 전용 assertion을 만들면 가독성이 크게 향상.

```java
public class SeatAssert extends AbstractAssert<SeatAssert, Seat> {

    public SeatAssert(Seat actual) { super(actual, SeatAssert.class); }

    public static SeatAssert assertThat(Seat actual) { return new SeatAssert(actual); }

    public SeatAssert isReservingBy(String userId) {
        isNotNull();
        if (actual.getStatus() != SeatStatus.RESERVING) {
            failWithMessage("Expected RESERVING, but was <%s>", actual.getStatus());
        }
        if (!userId.equals(actual.getUserId())) {
            failWithMessage("Expected owner <%s>, but was <%s>", userId, actual.getUserId());
        }
        return this;
    }
}

// 사용
SeatAssert.assertThat(seat).isReservingBy("user-1");
```

---

## 7. Best Practices

### 7.1 메서드 체이닝으로 의도를 한 줄에

```java
// Good
assertThat(concert.getTitle())
    .isNotBlank()
    .startsWith("BTS")
    .contains("콘서트");

// Bad
assertThat(concert.getTitle()).isNotBlank();
assertThat(concert.getTitle()).startsWith("BTS");
assertThat(concert.getTitle()).contains("콘서트");
```

### 7.2 `extracting`으로 컬렉션 검증 압축

```java
assertThat(concerts)
    .extracting(Concert::getId, Concert::getTitle)
    .containsExactly(
        tuple("c-1", "BTS"),
        tuple("c-2", "아이유")
    );
```

### 7.3 `@DisplayName`은 행위가 아니라 결과를 적는다

```java
// Bad — 무엇을 호출하는지만 적힘
@DisplayName("reserveSeat 호출")

// Good — 무슨 결과를 기대하는지가 적힘
@DisplayName("AVAILABLE 좌석을 예약하면 RESERVING으로 바뀌고 holdExpiresAt이 채워진다")
```

### 7.4 BusinessException은 errorCode까지 검증

단순히 `isInstanceOf(BusinessException.class)`만 검증하면 잘못된 분기로 떨어져도 통과한다. 항상 `extracting("errorCode")`까지.

```java
assertThatThrownBy(...)
    .isInstanceOf(BusinessException.class)
    .extracting("errorCode")
    .isEqualTo(SeatErrorCode.ALREADY_RESERVED);   // ← 이게 핵심
```

### 7.5 통합 테스트에서는 fixture 헬퍼를 적극 활용

각 테스트마다 user/concert/schedule/seat을 만드는 코드가 반복되면 `Fixtures` 클래스로 분리.

### 7.6 동시성 테스트는 반드시 timeout

`done.await()`에 timeout 없으면 deadlock 시 영원히 기다린다. 항상 `await(5, TimeUnit.SECONDS)` 같은 상한선.

---

## 8. 체크리스트

### 단위 테스트 (도메인 모델 / 엔티티)

- [ ] 정적 팩토리 (`create`, `from`) 초기 상태 검증
- [ ] 비즈니스 메서드 정상 흐름 + 예외 흐름 모두
- [ ] 예외는 `BusinessException` + 정확한 `ErrorCode` 까지 검증
- [ ] 경계값(null/empty/0/음수)
- [ ] `@Nested`로 메서드별 그룹화

### JPA 통합 테스트 (`@DataJpaTest`)

- [ ] 저장 → `em.clear()` → 조회 흐름 (1차 캐시 우회)
- [ ] ID 자동 생성 (`UUID`)
- [ ] Auditing (`@CreatedDate`/`@LastModifiedDate`) — 영속화 후 검증
- [ ] 제약조건(unique, nullable) 위반 시 `PersistenceException`
- [ ] 커스텀 `@Query` (예: `findExpiredHolds`, `findByIdWithLock`)
- [ ] 연관관계 매핑 (LAZY 페치 시 트랜잭션 내에서만 접근)

### 서비스 테스트

- [ ] Repository **인터페이스**를 mock (구현체 mock 금지)
- [ ] readOnly vs write 트랜잭션 메서드 분리 검증
- [ ] 예외 분기 (NOT_FOUND, FORBIDDEN 등) 모두 커버
- [ ] mock 호출 횟수/인자 검증 (`then(repo).should().findByIdWithLock(...)`)

### 유스케이스 통합 테스트

- [ ] 교차 도메인 흐름 (예: 예약 → 결제 → 좌석 상태 변화)
- [ ] 트랜잭션 롤백 시 모든 도메인이 일관되게 되돌려지는지
- [ ] 중복 예약/결제 등 비즈니스 룰 종단 검증

### 동시성 테스트

- [ ] `ExecutorService` + `CountDownLatch`로 race 강제
- [ ] `await` 항상 timeout 지정
- [ ] 성공/실패 수 정확히 검증 (n명 중 1명만 성공 등)
- [ ] Testcontainers MySQL로 실제 락 동작 확인 (H2 한계 회피)

---

## 참고

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Core Features](https://assertj.github.io/doc/#assertj-core-features)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Testcontainers for Java](https://java.testcontainers.org/)
