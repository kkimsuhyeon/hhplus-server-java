# AssertJ 학습 가이드

## 목차
1. [AssertJ란?](#assertj란)
2. [기본 사용법](#기본-사용법)
3. [주요 검증 메서드](#주요-검증-메서드)
4. [고급 기능](#고급-기능)
5. [실전 예제](#실전-예제)
6. [Best Practices](#best-practices)

---

## AssertJ란?

AssertJ는 Java용 유창한(fluent) API를 제공하는 assertion 라이브러리입니다.

### 주요 특징
- **가독성**: 메서드 체이닝으로 읽기 쉬운 테스트 코드 작성
- **풍부한 API**: 다양한 타입에 대한 전용 assertion 제공
- **IDE 지원**: 자동완성 및 타입 안전성 제공
- **에러 메시지**: 명확하고 상세한 실패 메시지

### 설치
Spring Boot Starter Test에 기본 포함되어 있습니다.

```gradle
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

---

## 기본 사용법

### 1. 기본 import

```java
import static org.assertj.core.api.Assertions.*;
```

### 2. 기본 구조

```java
assertThat(실제값).검증메서드(기대값);
```

### 3. 간단한 예제

```java
@Test
void basicExample() {
    String name = "홍길동";

    assertThat(name)
        .isNotNull()
        .isEqualTo("홍길동")
        .startsWith("홍")
        .endsWith("동");
}
```

---

## 주요 검증 메서드

### 1. 기본 검증

```java
// Null 체크
assertThat(object).isNull();
assertThat(object).isNotNull();

// 동등성 체크
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotEqualTo(expected);

// 같은 객체 참조 체크
assertThat(actual).isSameAs(expected);
assertThat(actual).isNotSameAs(expected);
```

### 2. 문자열 검증

```java
String text = "Hello World";

assertThat(text)
    .isNotEmpty()                    // 빈 문자열이 아님
    .isNotBlank()                    // 공백만 있는 문자열이 아님
    .hasSize(11)                     // 길이 확인
    .startsWith("Hello")             // 시작 문자열
    .endsWith("World")               // 끝 문자열
    .contains("lo Wo")               // 포함 문자열
    .doesNotContain("Goodbye")       // 미포함 문자열
    .matches("Hello.*")              // 정규식 매칭
    .isEqualToIgnoringCase("hello world"); // 대소문자 무시 비교
```

### 3. 숫자 검증

```java
int number = 10;
Long price = 150000L;

assertThat(number)
    .isPositive()                    // 양수
    .isNotNegative()                 // 0 이상
    .isGreaterThan(5)                // 초과
    .isGreaterThanOrEqualTo(10)      // 이상
    .isLessThan(20)                  // 미만
    .isLessThanOrEqualTo(10)         // 이하
    .isBetween(5, 15);               // 범위 내

assertThat(price)
    .isCloseTo(150000L, within(100L)); // 오차 범위 내 비교
```

### 4. Boolean 검증

```java
boolean result = true;

assertThat(result).isTrue();
assertThat(result).isNotFalse();
```

### 5. 날짜/시간 검증

```java
LocalDateTime now = LocalDateTime.now();
LocalDateTime future = now.plusDays(7);

assertThat(future)
    .isAfter(now)
    .isBefore(now.plusMonths(1))
    .isBetween(now, now.plusYears(1))
    .isInSameYearAs(now)
    .isInSameMonthAs(now);
```

### 6. 컬렉션 검증

```java
List<String> list = Arrays.asList("A", "B", "C");

// 기본 검증
assertThat(list)
    .isNotEmpty()
    .hasSize(3)
    .hasSizeGreaterThan(2)
    .hasSizeLessThan(5);

// 요소 포함 검증
assertThat(list)
    .contains("A", "B")              // 포함 (순서 무관)
    .containsOnly("A", "B", "C")     // 정확히 이 요소들만
    .containsExactly("A", "B", "C")  // 순서까지 정확히
    .containsExactlyInAnyOrder("C", "B", "A") // 순서 무관
    .doesNotContain("D")
    .doesNotContainNull();

// 조건 검증
assertThat(list)
    .allMatch(s -> s.length() == 1)  // 모든 요소가 조건 만족
    .anyMatch(s -> s.equals("A"))    // 하나라도 조건 만족
    .noneMatch(s -> s.equals("D"));  // 모든 요소가 조건 불만족
```

### 7. Enum 검증

```java
SeatStatus status = SeatStatus.AVAILABLE;

assertThat(status)
    .isEqualTo(SeatStatus.AVAILABLE)
    .isIn(SeatStatus.AVAILABLE, SeatStatus.RESERVING)
    .isNotIn(SeatStatus.RESERVED);
```

---

## 고급 기능

### 1. 객체 필드 추출 (extracting)

```java
// 단일 필드 추출
List<User> users = getUsers();
assertThat(users)
    .extracting("name")
    .containsExactly("홍길동", "김철수");

// 여러 필드 추출
assertThat(users)
    .extracting("name", "age")
    .containsExactly(
        tuple("홍길동", 20),
        tuple("김철수", 25)
    );

// 메서드 참조로 추출
assertThat(users)
    .extracting(User::getName, User::getAge)
    .containsExactly(
        tuple("홍길동", 20),
        tuple("김철수", 25)
    );
```

### 2. 필터링 (filteredOn)

```java
List<SeatEntity> seats = getSeats();

assertThat(seats)
    .filteredOn(SeatEntity::isAvailable)
    .hasSize(10);

assertThat(seats)
    .filteredOn("status", SeatStatus.AVAILABLE)
    .extracting("seatNumber")
    .containsExactly(1, 2, 3);

// 복잡한 조건
assertThat(seats)
    .filteredOn(seat -> seat.getPrice() > 100000)
    .filteredOn(SeatEntity::isAvailable)
    .hasSize(5);
```

### 3. satisfies와 allSatisfy

```java
// 단일 객체 검증
assertThat(concert)
    .satisfies(c -> {
        assertThat(c.getTitle()).isNotEmpty();
        assertThat(c.getDescription()).isNotEmpty();
        assertThat(c.getCreatedAt()).isNotNull();
    });

// 컬렉션의 모든 요소 검증
assertThat(concerts)
    .allSatisfy(concert -> {
        assertThat(concert.getId()).isNotNull();
        assertThat(concert.getTitle()).isNotEmpty();
    });

// 컬렉션에 특정 조건 만족하는 요소 있는지 검증
assertThat(concerts)
    .anySatisfy(concert ->
        assertThat(concert.getTitle()).contains("BTS")
    );

// 모든 요소가 조건을 만족하지 않는지 검증
assertThat(concerts)
    .noneSatisfy(concert ->
        assertThat(concert.getTitle()).isEmpty()
    );
```

### 4. 예외 검증

```java
// 기본 예외 검증
assertThatThrownBy(() -> service.getConcert(null))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("콘서트 ID는 필수입니다.")
    .hasMessageContaining("필수");

// 특정 예외 타입 검증
assertThatIllegalArgumentException()
    .isThrownBy(() -> service.getConcert(""))
    .withMessageContaining("필수");

assertThatNullPointerException()
    .isThrownBy(() -> processNull(null));

// 예외가 발생하지 않는지 검증
assertThatCode(() -> service.getConcert("valid-id"))
    .doesNotThrowAnyException();

// 예외가 없는 경우와 있는 경우 함께 검증
assertThatNoException()
    .isThrownBy(() -> service.getAllConcerts());
```

### 5. assertAll (JUnit 5)

여러 검증을 한 번에 실행하고 모든 실패를 확인합니다.

```java
@Test
void multipleAssertions() {
    Concert concert = getConcert();

    assertAll(
        () -> assertThat(concert.getId()).isNotNull(),
        () -> assertThat(concert.getTitle()).isEqualTo("BTS 콘서트"),
        () -> assertThat(concert.getDescription()).isNotEmpty(),
        () -> assertThat(concert.getCreatedAt()).isNotNull()
    );
}
```

### 6. Soft Assertions

모든 검증을 실행하고 마지막에 한 번에 실패를 보고합니다.

```java
@Test
void softAssertionsExample() {
    SoftAssertions softly = new SoftAssertions();

    Concert concert = getConcert();

    softly.assertThat(concert.getId()).isNotNull();
    softly.assertThat(concert.getTitle()).isEqualTo("BTS 콘서트");
    softly.assertThat(concert.getDescription()).isNotEmpty();

    softly.assertAll(); // 여기서 모든 실패를 한 번에 보고
}

// JUnit 5 Extension 사용
@ExtendWith(SoftAssertionsExtension.class)
class MyTest {
    @Test
    void test(SoftAssertions softly) {
        softly.assertThat(actual).isEqualTo(expected);
        // assertAll() 자동 호출
    }
}
```

### 7. Custom Assertions

도메인 특화 assertion을 만들 수 있습니다.

```java
public class ConcertAssert extends AbstractAssert<ConcertAssert, ConcertEntity> {

    public ConcertAssert(ConcertEntity actual) {
        super(actual, ConcertAssert.class);
    }

    public static ConcertAssert assertThat(ConcertEntity actual) {
        return new ConcertAssert(actual);
    }

    public ConcertAssert hasTitle(String title) {
        isNotNull();
        if (!actual.getTitle().equals(title)) {
            failWithMessage("Expected title to be <%s> but was <%s>",
                title, actual.getTitle());
        }
        return this;
    }

    public ConcertAssert isUpcoming() {
        isNotNull();
        // 스케줄 중 미래의 날짜가 있는지 확인하는 로직
        return this;
    }
}

// 사용
ConcertAssert.assertThat(concert)
    .hasTitle("BTS 콘서트")
    .isUpcoming();
```

---

## 실전 예제

### 1. Service 계층 테스트

```java
@Test
@DisplayName("콘서트 조회 시 존재하지 않으면 예외 발생")
void getConcert_NotFound_ThrowsException() {
    // given
    given(repository.findById("non-existent"))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> service.getConcert("non-existent"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("콘서트를 찾을 수 없습니다")
        .hasMessageContaining("non-existent");

    then(repository).should(times(1)).findById("non-existent");
}
```

### 2. 복잡한 객체 검증

```java
@Test
@DisplayName("좌석 예약 성공 시 상태가 올바르게 변경된다")
void reserveSeat_Success() {
    // given
    SeatEntity seat = createAvailableSeat();
    given(repository.findSeatById("seat-1"))
        .willReturn(Optional.of(seat));
    given(repository.saveSeat(any()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    SeatEntity result = service.reserveSeat("seat-1", "user-1");

    // then
    assertThat(result)
        .isNotNull()
        .satisfies(s -> {
            assertThat(s.getStatus()).isEqualTo(SeatStatus.RESERVING);
            assertThat(s.isAvailable()).isFalse();
            assertThat(s.getUser()).isNotNull();
            assertThat(s.getUser().getId()).isEqualTo("user-1");
        });
}
```

### 3. 컬렉션 처리 검증

```java
@Test
@DisplayName("예약 가능한 좌석만 필터링된다")
void getAvailableSeats_FiltersCorrectly() {
    // given
    List<SeatEntity> allSeats = Arrays.asList(
        createSeat(1, SeatStatus.AVAILABLE),
        createSeat(2, SeatStatus.RESERVED),
        createSeat(3, SeatStatus.AVAILABLE),
        createSeat(4, SeatStatus.RESERVING)
    );

    given(repository.findAvailableSeats("schedule-1"))
        .willReturn(allSeats.stream()
            .filter(SeatEntity::isAvailable)
            .collect(Collectors.toList()));

    // when
    List<SeatEntity> result = service.getAvailableSeats("schedule-1");

    // then
    assertThat(result)
        .hasSize(2)
        .extracting("seatNumber", "status")
        .containsExactlyInAnyOrder(
            tuple(1, SeatStatus.AVAILABLE),
            tuple(3, SeatStatus.AVAILABLE)
        )
        .allSatisfy(seat ->
            assertThat(seat.isAvailable()).isTrue()
        );
}
```

### 4. 경계값 테스트

```java
@Test
@DisplayName("빈 문자열과 공백 문자열 구분 테스트")
void validateInput() {
    assertAll(
        // null 체크
        () -> assertThatIllegalArgumentException()
            .isThrownBy(() -> service.getConcert(null))
            .withMessageContaining("필수"),

        // 빈 문자열 체크
        () -> assertThatIllegalArgumentException()
            .isThrownBy(() -> service.getConcert(""))
            .withMessageContaining("필수"),

        // 공백 문자열 체크
        () -> assertThatIllegalArgumentException()
            .isThrownBy(() -> service.getConcert("   "))
            .withMessageContaining("필수")
    );
}
```

---

## Best Practices

### 1. 메서드 체이닝 활용

```java
// Good: 체이닝으로 여러 검증을 명확하게 표현
assertThat(concert.getTitle())
    .isNotNull()
    .isNotEmpty()
    .startsWith("BTS")
    .contains("콘서트");

// Bad: 여러 번 assertThat 호출
assertThat(concert.getTitle()).isNotNull();
assertThat(concert.getTitle()).isNotEmpty();
assertThat(concert.getTitle()).startsWith("BTS");
```

### 2. extracting 적극 활용

```java
// Good: extracting으로 간결하게
assertThat(concerts)
    .extracting("id", "title")
    .containsExactly(
        tuple("concert-1", "BTS 콘서트"),
        tuple("concert-2", "아이유 콘서트")
    );

// Bad: 반복문으로 검증
List<ConcertEntity> concerts = service.getAllConcerts();
assertThat(concerts.get(0).getId()).isEqualTo("concert-1");
assertThat(concerts.get(0).getTitle()).isEqualTo("BTS 콘서트");
// ...
```

### 3. 적절한 예외 검증 메서드 사용

```java
// Good: 구체적인 예외 타입 메서드 사용
assertThatIllegalArgumentException()
    .isThrownBy(() -> service.getConcert(null))
    .withMessageContaining("필수");

// OK: 일반적인 방법
assertThatThrownBy(() -> service.getConcert(null))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("필수");
```

### 4. 의미있는 변수명과 Given-When-Then 패턴

```java
@Test
@DisplayName("유효한 스케줄 ID로 예약 가능한 좌석을 조회할 수 있다")
void getAvailableSeats_WithValidScheduleId_ReturnsSeats() {
    // given - 준비
    String validScheduleId = "schedule-1";
    List<SeatEntity> expectedSeats = createAvailableSeats();
    given(repository.findAvailableSeats(validScheduleId))
        .willReturn(expectedSeats);

    // when - 실행
    List<SeatEntity> actualSeats = service.getAvailableSeats(validScheduleId);

    // then - 검증
    assertThat(actualSeats)
        .hasSize(expectedSeats.size())
        .allMatch(SeatEntity::isAvailable);
}
```

### 5. @DisplayName으로 명확한 의도 표현

```java
@Nested
@DisplayName("reserveSeat 메서드는")
class ReserveSeatTest {

    @Test
    @DisplayName("예약 가능한 좌석을 예약할 수 있다")
    void success() { ... }

    @Test
    @DisplayName("이미 예약된 좌석은 예약할 수 없다")
    void alreadyReserved() { ... }

    @Test
    @DisplayName("존재하지 않는 좌석은 예약할 수 없다")
    void notFound() { ... }
}
```

---

## 참고 자료

- [AssertJ 공식 문서](https://assertj.github.io/doc/)
- [AssertJ Core Features](https://assertj.github.io/doc/#assertj-core-features)
- [Joel Costigliola's AssertJ Guide](https://www.baeldung.com/introduction-to-assertj)

---

## 마치며

AssertJ는 테스트 코드의 가독성과 유지보수성을 크게 향상시킵니다.
처음에는 익숙하지 않을 수 있지만, IDE의 자동완성 기능을 활용하면서
사용하다 보면 빠르게 익숙해질 수 있습니다.

**핵심은 fluent API를 통한 메서드 체이닝과 다양한 검증 메서드를
적재적소에 활용하는 것입니다!**