# Entity 테스트 코드 작성 가이드

## 📚 목차
1. [테스트 종류와 차이점](#테스트-종류와-차이점)
2. [단위 테스트 작성법](#단위-테스트-작성법)
3. [JPA 통합 테스트 작성법](#jpa-통합-테스트-작성법)
4. [테스트 작성 시 주의사항](#테스트-작성-시-주의사항)
5. [자주 사용하는 Assertion](#자주-사용하는-assertion)

---

## 테스트 종류와 차이점

### 1️⃣ 단위 테스트 (Unit Test)
**파일**: `ConcertEntityTest.java`

**특징**:
- 🚀 **빠르다**: DB 연결 없이 메모리에서만 실행
- 🎯 **범위가 작다**: Entity 객체의 메서드만 테스트
- 🔧 **간단하다**: 외부 의존성 없음

**언제 사용하나요?**
```java
✅ Entity의 생성자/빌더 테스트
✅ Getter/Setter 테스트
✅ 비즈니스 로직 메서드 테스트 (isReservable() 같은)
✅ 계산 로직 테스트
```

**예시**:
```java
@Test
void createConcertEntityWithBuilder() {
    // DB 없이 순수 객체만 테스트
    ConcertEntity concert = ConcertEntity.builder()
            .title("아이유 콘서트")
            .description("설명")
            .build();
    
    assertThat(concert.getTitle()).isEqualTo("아이유 콘서트");
}
```

---

### 2️⃣ JPA 통합 테스트 (Integration Test)
**파일**: `ConcertEntityJpaTest.java`

**특징**:
- 🐢 **느리다**: 실제 DB(H2 인메모리)와 통신
- 🌐 **범위가 크다**: JPA, 영속성 컨텍스트, 트랜잭션 등 포함
- 🔍 **복잡하다**: Spring Context 로딩 필요

**언제 사용하나요?**
```java
✅ DB 저장/조회가 잘 되는지 확인
✅ JPA 어노테이션이 제대로 동작하는지 확인
✅ 연관관계 매핑 테스트
✅ Auditing(@CreatedDate 등) 테스트
✅ DB 제약조건(nullable, unique 등) 테스트
```

**예시**:
```java
@DataJpaTest  // JPA 관련 빈만 로드
@Test
void saveAndFindConcert() {
    // 실제 DB에 저장하고 조회
    ConcertEntity concert = ConcertEntity.builder()
            .title("아이유 콘서트")
            .description("설명")
            .build();
    
    ConcertEntity saved = entityManager.persistAndFlush(concert);
    
    assertThat(saved.getId()).isNotNull(); // UUID 자동 생성 확인
    assertThat(saved.getCreatedAt()).isNotNull(); // Auditing 확인
}
```

---

## 단위 테스트 작성법

### 1. Given-When-Then 패턴 사용

```java
@Test
void testMethod() {
    // given: 테스트에 필요한 데이터 준비
    String title = "콘서트 제목";
    
    // when: 테스트할 동작 실행
    ConcertEntity concert = ConcertEntity.builder()
            .title(title)
            .build();
    
    // then: 결과 검증
    assertThat(concert.getTitle()).isEqualTo(title);
}
```

### 2. @Nested로 테스트 그룹화

```java
@Nested
@DisplayName("Entity 생성 테스트")
class CreateEntityTest {
    
    @Test
    @DisplayName("Builder로 생성할 수 있다")
    void createWithBuilder() {
        // 테스트 코드
    }
    
    @Test
    @DisplayName("필수 필드를 포함해야 한다")
    void createWithRequiredFields() {
        // 테스트 코드
    }
}
```

**장점**:
- 📁 관련된 테스트끼리 묶어서 관리
- 📖 테스트 리포트가 계층적으로 보기 좋음
- 🔧 같은 설정을 그룹 내에서 공유 가능

### 3. 테스트 헬퍼 메서드 활용

```java
// 반복되는 객체 생성 로직을 메서드로 추출
private ConcertEntity createConcertWithSchedules(boolean... reservableFlags) {
    List<ConcertScheduleEntity> schedules = new ArrayList<>();
    
    for (boolean isReservable : reservableFlags) {
        schedules.add(createMockSchedule(isReservable));
    }
    
    return ConcertEntity.builder()
            .title("테스트 콘서트")
            .concertSchedules(schedules)
            .build();
}
```

---

## JPA 통합 테스트 작성법

### 1. @DataJpaTest 사용

```java
@DataJpaTest  // JPA 관련 컴포넌트만 로드
@ActiveProfiles("test")  // test 프로파일 사용
class ConcertEntityJpaTest {
    
    @Autowired
    private TestEntityManager entityManager;  // 테스트용 EntityManager
}
```

**@DataJpaTest가 해주는 일**:
- ✅ H2 인메모리 DB 자동 설정
- ✅ JPA 관련 빈만 로드 (빠른 테스트)
- ✅ 각 테스트마다 자동 롤백 (격리성 보장)
- ✅ @Transactional 자동 적용

### 2. TestEntityManager 활용

```java
@Test
void saveAndFind() {
    // 1. 엔티티 저장
    ConcertEntity saved = entityManager.persistAndFlush(concert);
    
    // 2. 1차 캐시 초기화 (실제 DB 조회 강제)
    entityManager.clear();
    
    // 3. DB에서 다시 조회
    ConcertEntity found = entityManager.find(ConcertEntity.class, saved.getId());
    
    assertThat(found).isNotNull();
}
```

**왜 clear()를 사용하나요?**
```java
// clear() 없이 테스트하면?
ConcertEntity saved = entityManager.persist(concert);
ConcertEntity found = entityManager.find(Concert.class, saved.getId());
// 🚨 1차 캐시에서 가져오므로 실제 DB 조회가 안 됨!

// clear() 사용하면?
entityManager.clear();  // 1차 캐시 비우기
ConcertEntity found = entityManager.find(Concert.class, saved.getId());
// ✅ DB에서 실제로 조회함!
```

### 3. DB 제약조건 테스트

```java
@Test
@DisplayName("title이 null이면 예외가 발생한다")
void throwExceptionWhenTitleIsNull() {
    // given
    ConcertEntity concert = ConcertEntity.builder()
            .title(null)  // @Column(nullable = false) 위반
            .description("설명")
            .build();
    
    // when & then
    assertThrows(PersistenceException.class, () -> {
        entityManager.persistAndFlush(concert);
    });
}
```

---

## 테스트 작성 시 주의사항

### ⚠️ 1. Lombok의 @Builder와 불변성

ConcertEntity는 `@Builder`를 사용하므로 **수정이 불가능**합니다.

```java
// ❌ 이렇게 하면 안 됨 (setter가 없음)
concert.setTitle("새 제목");

// ✅ 새로운 객체를 만들어야 함
ConcertEntity updated = ConcertEntity.builder()
        .id(concert.getId())
        .title("새 제목")
        .description(concert.getDescription())
        .build();
```

### ⚠️ 2. Mock 객체 vs 실제 객체

**단위 테스트**: Mock 사용 가능
```java
// 단순히 isReservable() 동작만 확인하면 되므로
ConcertScheduleEntity mockSchedule = new ConcertScheduleEntity() {
    @Override
    public boolean isReservable() {
        return true;
    }
};
```

**통합 테스트**: 실제 객체 사용
```java
// 실제로 DB에 저장 가능한 완전한 객체 필요
ConcertScheduleEntity schedule = ConcertScheduleEntity.builder()
        .concert(concert)
        .concertDate(LocalDateTime.now())
        .build();
```

### ⚠️ 3. Auditing 테스트 시 주의

```java
// ❌ 이렇게 하면 Auditing이 동작하지 않음
@Test
void testAuditing() {
    ConcertEntity concert = ConcertEntity.builder()
            .title("제목")
            .build();
    
    // createdAt이 null! (영속화하지 않았기 때문)
    assertThat(concert.getCreatedAt()).isNull();
}

// ✅ 영속화해야 Auditing 동작
@Test
void testAuditing() {
    ConcertEntity concert = ConcertEntity.builder()
            .title("제목")
            .build();
    
    ConcertEntity saved = entityManager.persistAndFlush(concert);
    
    // 이제 createdAt이 설정됨!
    assertThat(saved.getCreatedAt()).isNotNull();
}
```

---

## 자주 사용하는 Assertion

### AssertJ 라이브러리 (권장)

```java
import static org.assertj.core.api.Assertions.*;

// 기본 검증
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotNull();
assertThat(actual).isNull();

// 문자열 검증
assertThat(title).isNotEmpty();
assertThat(title).isNotBlank();
assertThat(title).contains("콘서트");
assertThat(title).startsWith("아이유");

// 숫자 검증
assertThat(count).isGreaterThan(0);
assertThat(count).isLessThanOrEqualTo(100);
assertThat(price).isBetween(10000, 50000);

// 컬렉션 검증
assertThat(list).hasSize(3);
assertThat(list).isEmpty();
assertThat(list).contains(item1, item2);
assertThat(list).doesNotContain(item3);

// Boolean 검증
assertThat(result).isTrue();
assertThat(result).isFalse();

// 예외 검증
assertThatThrownBy(() -> {
    // 예외를 던질 코드
}).isInstanceOf(IllegalArgumentException.class)
  .hasMessage("에러 메시지");

// 시간 검증
assertThat(createdAt).isBeforeOrEqualTo(updatedAt);
assertThat(date).isBefore(LocalDateTime.now());
assertThat(date).isAfter(past);
```

### JUnit 5 Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

// 기본 검증
assertEquals(expected, actual);
assertNotNull(actual);
assertTrue(condition);

// 여러 검증을 한 번에
assertAll(
    () -> assertEquals("제목", concert.getTitle()),
    () -> assertNotNull(concert.getId()),
    () -> assertTrue(concert.isReservable())
);

// 예외 검증
assertThrows(IllegalArgumentException.class, () -> {
    // 예외를 던질 코드
});
```

---

## 🎯 테스트 작성 체크리스트

Entity 테스트를 작성할 때 다음을 확인하세요:

### 단위 테스트
- [ ] Builder로 객체 생성 가능한지
- [ ] 모든 필수 필드가 설정되는지
- [ ] Getter가 올바른 값을 반환하는지
- [ ] 비즈니스 로직 메서드가 정상 동작하는지
- [ ] 경계값 테스트 (null, 빈 리스트 등)

### JPA 통합 테스트
- [ ] DB 저장/조회가 정상 동작하는지
- [ ] ID가 자동 생성되는지
- [ ] Auditing이 동작하는지
- [ ] 제약조건(nullable, unique 등) 검증
- [ ] 연관관계 매핑이 정상 동작하는지

---

## 📖 추가 학습 자료

1. **JUnit 5 공식 문서**: https://junit.org/junit5/docs/current/user-guide/
2. **AssertJ 공식 문서**: https://assertj.github.io/doc/
3. **Spring Boot 테스트**: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing

---

## 💡 팁

### 테스트 이름 작성법
```java
// ❌ 나쁜 예
@Test
void test1() { ... }

// ✅ 좋은 예
@Test
@DisplayName("예약 가능한 스케줄이 하나라도 있으면 true를 반환한다")
void returnTrueWhenHasReservableSchedule() { ... }
```

### 테스트 실행 순서
1. 먼저 **단위 테스트**를 작성하고 실행
2. 단위 테스트가 모두 통과하면 **JPA 통합 테스트** 작성
3. 통합 테스트는 필요한 경우에만 작성 (모든 것을 테스트할 필요 없음)

### 테스트 커버리지
- Entity의 핵심 로직은 **반드시** 테스트
- 단순 Getter/Setter는 테스트 생략 가능
- 복잡한 비즈니스 로직은 **여러 케이스** 테스트
