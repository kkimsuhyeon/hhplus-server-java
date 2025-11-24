# 클린 아키텍처 리팩토링 작업 현황

이 문서는 다음 Claude Code 세션에 작업 내용을 전달하기 위한 문서입니다.

## 🎯 작업 목표
UseCase가 JPA를 전혀 모르도록 순수 도메인 모델과 Repository 인터페이스만 사용하도록 분리

---

## ✅ 완료된 작업

### 0. 전체 도메인 리팩토링 완료 ✅ (2024-11-24)

모든 도메인이 클린 아키텍처 패턴으로 리팩토링 완료되었습니다.

### 1. User 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/user/model/User.java` (순수 도메인 모델)
- `src/main/java/kr/hhplus/be/server/domain/user/application/UserRepository.java` (인터페이스)
- `src/main/java/kr/hhplus/be/server/domain/user/adapter/persistence/UserJpaEntity.java`
- `src/main/java/kr/hhplus/be/server/domain/user/adapter/persistence/UserMapper.java`
- `src/main/java/kr/hhplus/be/server/domain/user/adapter/persistence/UserRepositoryAdapter.java`

**핵심 내용:**
- User.java에 `addBalance()`, `deductBalance()` 비즈니스 로직
- JPA 어노테이션 없음, 순수 POJO
- 연관 Entity 참조 대신 ID만 보유

### 2. Reservation 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/reservation/model/Reservation.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/model/ReservationStatus.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/application/ReservationRepository.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/adapter/persistence/ReservationJpaEntity.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/adapter/persistence/ReservationJpaRepository.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/adapter/persistence/ReservationMapper.java`
- `src/main/java/kr/hhplus/be/server/domain/reservation/adapter/persistence/ReservationRepositoryAdapter.java`

**핵심 내용:**
- Reservation.java에 `completePayment()`, `validateForPayment()` 등 비즈니스 로직
- **중요:** `seatPrice` 필드 추가 - 예약 당시 가격 스냅샷 저장
- ReservationJpaEntity에 `seatPrice` 컬럼 추가

### 3. Seat 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/concert/model/Seat.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/model/SeatStatus.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/application/SeatRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/SeatJpaEntity.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/SeatJpaRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/SeatMapper.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/SeatRepositoryAdapter.java` ✅

### 5. Concert 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/concert/model/Concert.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/application/ConcertRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertJpaRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertMapper.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertRepositoryAdapter.java` ✅

### 6. ConcertSchedule 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/concert/model/ConcertSchedule.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/application/ConcertScheduleRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertScheduleJpaRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertScheduleMapper.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/concert/adapter/persistence/ConcertScheduleRepositoryAdapter.java` ✅

### 7. Payment 도메인 완전 분리 ✅

**생성된 파일:**
- `src/main/java/kr/hhplus/be/server/domain/payment/model/Payment.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/model/PaymentStatus.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/application/PaymentRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/adapter/persistence/PaymentJpaEntity.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/adapter/persistence/PaymentJpaRepository.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/adapter/persistence/PaymentMapper.java` ✅
- `src/main/java/kr/hhplus/be/server/domain/payment/adapter/persistence/PaymentRepositoryAdapter.java` ✅

### 8. ReservationUseCase 리팩토링 ✅

**수정된 파일:**
- `src/main/java/kr/hhplus/be/server/application/usecase/ReservationUseCase.java` ✅
  - JPA Entity 직접 사용 → 순수 도메인 모델 사용
  - `SeatService`, `UserService` 의존 → `SeatRepository`, `UserRepository`, `ReservationRepository` 의존

### 4. PaymentUseCase 수정 완료 ✅

**파일:** `src/main/java/kr/hhplus/be/server/application/usecase/PaymentUseCase.java`

**변경 내용:**
- JPA Entity 직접 사용 → 순수 도메인 모델 사용
- `ReservationService`, `UserService` 의존 → `ReservationRepository`, `UserRepository` 의존
- `reservation.getSeat().getPrice()` → `reservation.getSeatPrice()` (스냅샷 사용)
- 명시적인 `save()` 호출

---

## 📋 다음 작업 (선택적 개선사항)

### 1. 🟢 기존 Entity 완전 제거
- 현재는 기존 Entity와 새 구조가 공존
- 완전 전환 후 기존 `model/entity/` 디렉토리 제거
- Adapter에서 새 JpaEntity 직접 사용

### 2. 🟢 테스트 코드 정비
- ReservationUseCaseTest 수정 (Mock 기반 단위 테스트)
- 통합 테스트 추가

### 3. 🟢 기타 UseCase 리팩토링
- 다른 UseCase들도 순수 도메인 모델 사용하도록 변경

---

## 🎯 리팩토링 패턴 (모든 도메인 공통)

### 5단계 패턴:

1. **순수 도메인 모델** (`domain/{domain}/model/{Domain}.java`)
   - JPA 어노테이션 없음
   - 비즈니스 로직만 포함
   - 연관 Entity는 ID만 보유

2. **Repository 인터페이스** (`domain/{domain}/application/{Domain}Repository.java`)
   - `findById()`, `save()` 등 기본 메서드
   - 순수 도메인 모델만 다룸

3. **JPA Entity** (`domain/{domain}/adapter/persistence/{Domain}JpaEntity.java`)
   - JPA 어노테이션만
   - 비즈니스 로직 없음
   - `updateFromDomain()` 메서드 (Adapter용)

4. **Mapper** (`domain/{domain}/adapter/persistence/{Domain}Mapper.java`)
   - `toDomain()`: Entity → Model
   - `updateEntity()`: Model → Entity

5. **Repository Adapter** (`domain/{domain}/adapter/persistence/{Domain}RepositoryAdapter.java`)
   - Repository 인터페이스 구현
   - 기존 Entity와 새 구조의 브릿지
   - 더티 체킹 활용

---

## 🔑 핵심 원칙

### 1. UseCase는 JPA를 모른다
- Repository 인터페이스만 의존
- 순수 도메인 모델만 사용
- JPA Entity에 대해 전혀 알지 못함

### 2. 도메인 모델은 순수하다
- JPA 어노테이션 없음 (POJO)
- 연관 Entity 참조 대신 ID만 보유
- 비즈니스 로직에만 집중

### 3. 연관관계는 ID만 보유

**도메인 모델:**
```java
public class Reservation {
    private String userId;    // User 객체 아닌 ID만
    private String seatId;    // Seat 객체 아닌 ID만
}
```

**JPA Entity:**
```java
public class ReservationJpaEntity {
    @ManyToOne
    private UserEntity user;     // JPA 연관관계 유지
    @ManyToOne
    private SeatEntity seat;     // JPA 연관관계 유지
}
```

**Mapper에서 변환:**
```java
public Reservation toDomain(ReservationJpaEntity entity) {
    return Reservation.builder()
        .userId(entity.getUser().getId())    // ID만 추출
        .seatId(entity.getSeat().getId())    // ID만 추출
        .build();
}
```

### 4. Adapter가 기술 복잡도 격리
- Entity ↔ Model 변환
- JPA 특성(영속성, 더티 체킹) 활용
- 기존 Entity와의 브릿지 역할

### 5. 더티 체킹 활용

```java
@Override
public Reservation save(Reservation reservation) {
    if (reservation.getId() != null) {
        // 기존 Entity 조회 (영속 상태)
        ReservationJpaEntity entity = jpaRepository.findById(...)
            .orElseThrow(...);
        
        // 변경사항 반영
        mapper.updateEntity(entity, reservation);
        
        // save() 호출 안 해도 됨! (더티 체킹)
        // 하지만 명시적으로 호출해도 무방
    } else {
        // 신규 생성
        // ...
    }
}
```

### 6. 시점 데이터는 스냅샷 저장
- Reservation의 `seatPrice`: 예약 당시 가격 저장
- 원본 Seat 가격이 변경되어도 예약 가격은 불변
- 비즈니스 규칙: 예약 시점 가격으로 결제

---

## 💡 중요한 기술적 결정사항

### 예약 당시 가격 저장 (seatPrice)

**이유:**
- Seat 가격이 변경되어도 예약 당시 가격으로 결제
- 예약 시점의 계약 가격 보존

**구현:**
```java
// Reservation 도메인 모델
private BigInteger seatPrice;  // 예약 당시 좌석 가격

// Adapter에서 저장 시
entity = ReservationJpaEntity.builder()
    .seatPrice(seat.getPrice())  // 현재 가격을 복사
    .build();
```

### 기존 코드와의 공존

현재는 기존 Entity(`model/entity/`)와 새 구조가 공존:
- Adapter에서 기존 Entity 조회 → 새 Entity로 변환
- 점진적 전환
- 완전 전환 후 기존 Entity 제거 예정

---

## 📝 작업 시 주의사항

### 테스트 코드
- 순수 도메인 모델: JPA 없이 단위 테스트 가능
- Adapter: 통합 테스트 필요
- UseCase: Repository Mock으로 단위 테스트

### Service vs UseCase
- 기존: `ReservationService`, `UserService`
- 새: `PaymentUseCase`
- 점진적으로 Service → UseCase 전환

---

## 🚀 다음 Claude Code 세션 시작 방법

### 시작 명령:
```
C:\Users\FINGER\workspace\hhplus-server-java 프로젝트의 
REFACTORING_STATUS.md 문서를 읽고 
나머지 도메인 리팩토링을 계속 진행해줘.

우선순위:
1. Seat 도메인 완성 (SeatMapper, SeatRepositoryAdapter)
2. Concert 도메인 리팩토링
3. ConcertSchedule 도메인 리팩토링
4. Payment 도메인 리팩토링
5. ReservationUseCase 리팩토링
```

---

## 📚 참고 정보

- **원본 피드백:** PR #3에 대한 코드 리뷰
- **핵심 개념:** 클린 아키텍처, 헥사고날 아키텍처, 도메인 순수성
- **학습 목표:** OOP와 의존성 관리 깊이 이해

---

**작성일:** 2024년 11월
**작성자:** Claude (리팩토링 작업 수행)
**프로젝트:** hhplus-server-java
**경로:** C:\Users\FINGER\workspace\hhplus-server-java
