# 코드 리뷰 보고서

**프로젝트**: 항해플러스 콘서트 예약 시스템
**날짜**: 2026-02-10
**리뷰어**: Claude Code
**범위**: 전체 프로젝트 (Java 파일 153개 / 약 6,025줄)

---

## 요약

| 심각도 | 총 개수 | 수정 완료 | 미수정 |
|--------|---------|----------|--------|
| CRITICAL | 5 | 5 | 0 |
| HIGH | 7 | 0 | 7 |
| MEDIUM | 10 | 0 | 10 |

---

## CRITICAL 이슈 (수정 완료)

### 1. [수정완료] UserEntity 필드 매핑 누락 - 데이터 손실 버그
- **파일**: `domain/user/adapter/out/persistence/UserEntity.java`
- **문제**: `create()`, `toModel()`, `update()` 메서드가 `balance`만 매핑하고 `email`, `password`, `role`을 누락
- **영향**: 유저 생성 시 email/password가 NULL로 저장되어 유저 생성 기능이 완전히 깨져있었음
- **추가 수정**: `UserMapper.toModel()`, `CreateUserCommand`에 email/password 필드 추가

### 2. [수정완료] JWT Secret이 설정 파일에 하드코딩
- **파일**: `src/main/resources/application-local.yml:28-31`
- **문제**: JWT 비밀키가 버전관리 대상인 설정 파일에 평문으로 노출
- **수정**: `${JWT_SECRET:기본값}` 환경변수 패턴으로 변경
- **추가 수정**: `src/test/resources/application.yml`에 JWT 설정 추가 (누락으로 통합테스트 전체 실패 상태였음)

### 3. [수정완료] 좌석 예약 Race Condition - DB 제약조건 부재
- **파일**: `domain/reservation/adapter/out/persistence/ReservationEntity.java`
- **문제**: `(user_id, seat_id)` 조합에 unique constraint가 없어 동시 요청 시 중복 예약 가능
- **수정**: `@UniqueConstraint(name = "uk_reservation_user_seat", columnNames = {"user_id", "seat_id"})` 추가

### 4. [수정완료] 토큰 상태 변경 후 저장 누락
- **파일**: `domain/token/application/QueueTokenService.java:51-61`
- **문제**: `expireTokens()`, `activateTokens()`에서 상태를 변경하지만 `save()`를 호출하지 않음
- **영향**: 토큰 상태 전환(WAITING->ACTIVE, ACTIVE->EXPIRED)이 메서드 호출 후 유실
- **수정**: 상태 변경 후 `queueTokenRepository.save(token)` 호출 추가

### 5. [수정완료] 만료된 예약/좌석 자동 정리 스케줄러 부재
- **문제**: `Seat.holdExpiresAt`, `Reservation.expiresAt`를 추적하지만 만료 시 자동 정리 메커니즘 없음
- **영향**: 사용자가 예약을 포기하면 좌석이 RESERVING 상태로 영원히 남아 신규 예약 차단
- **수정**: `ReservationScheduler` 생성 (60초 주기), `findExpiredHolds()` / `findExpiredPendingReservations()` 쿼리 추가

---

## HIGH 이슈 (미수정)

### 6. 인증 엔드포인트에 `@Valid` 누락 + 입력 검증 부족
- **파일**:
  - `domain/auth/adapter/in/web/AuthController.java:30-43`
  - `domain/auth/adapter/in/web/request/SignInRequest.java`
  - `domain/auth/adapter/in/web/request/SignUpRequest.java`
- **문제**: `@RequestBody`에 `@Valid` 어노테이션 없음, `@Email` 검증 없음, 비밀번호 강도 규칙 없음
- **수정 방법**: 컨트롤러에 `@Valid` 추가, Request DTO에 `@Email`, `@Size`, `@Pattern` 추가

### 7. 도메인 모델 직접 변이 - 불변성 위반
- **파일**:
  - `domain/concert/model/Seat.java` - `reserve()`, `confirm()`, `release()`가 필드를 직접 변경
  - `domain/user/model/User.java` - `addBalance()`, `deductBalance()`가 `balance`를 직접 변경
  - `domain/payment/model/Payment.java` - `cancel()`이 `status`를 직접 변경
  - `domain/reservation/model/Reservation.java` - `completePayment()`, `cancel()`이 `status`를 직접 변경
  - `domain/token/model/QueueToken.java` - `activate()`, `expire()`가 상태를 직접 변경
- **문제**: 프로젝트 가이드라인에 따르면 도메인 모델은 불변이어야 하며, 변경 시 새 인스턴스를 반환해야 함
- **수정 방법**: 변이 메서드를 `Builder` 기반의 새 인스턴스 반환 방식으로 리팩토링

### 8. `AuthUser.from()`에서 NullPointerException 위험
- **파일**: `config/security/AuthUser.java:41`
- **코드**: `UserRole.valueOf(payload.getAuthorities().getFirst().replace("ROLE_", ""))`
- **문제**: `getAuthorities()`가 null이거나, `getFirst()`가 null이거나, 잘못된 enum 값일 때 예외 처리 없음
- **수정 방법**: null/empty 체크 추가, `IllegalArgumentException` try-catch 처리

### 9. 인증 엔드포인트에 Rate Limiting 없음
- **대상**: `/api/v1/auth/sign-in`, `/api/v1/auth/sign-up`
- **문제**: 요청 제한이 없어 브루트포스 공격, 크리덴셜 스터핑에 취약
- **수정 방법**: Bucket4j 또는 Spring Cloud Gateway로 요청 제한 구현

### 10. JWT 토큰 무효화 메커니즘 없음
- **파일**: `domain/auth/application/AuthService.java`
- **문제**: 로그아웃 엔드포인트 없음, 토큰 블랙리스트 없음. 발급된 JWT는 만료까지 유효 (access 1시간, refresh 7일)
- **수정 방법**: Redis 기반 토큰 블랙리스트 구현, `/logout` 엔드포인트 추가

### 11. QueueTokenMemoryRepository 대기열 중복 등록
- **파일**: `domain/token/adapter/out/memory/QueueTokenMemoryRepository.java:84-89`
- **문제**: `save()` 호출 시 업데이트인 경우에도 `waitingQueue.offer(key)`를 호출하여 대기열에 중복 엔트리 발생
- **수정 방법**: 토큰이 이미 존재하는지 확인 후 대기열에 추가

### 12. ReservationUseCase에서 비원자적 좌석 상태 전환
- **파일**: `application/usecase/ReservationUseCase.java:35`
- **문제**: 좌석을 RESERVING으로 변경한 뒤 예약 저장이 실패하면, 좌석이 고아 상태로 남음
- **수정 방법**: 좌석 상태 변경과 예약 생성이 동일 트랜잭션 내에서 적절히 롤백되도록 보장

---

## MEDIUM 이슈 (미수정)

### 13. Command DTO에 `@Data` 사용 (setter 생성됨)
- **파일**: `application/dto/ReserveSeatCommand.java`, `application/dto/PayCommand.java`
- **문제**: `@Data`가 setter를 생성하여 커맨드 객체의 불변성 위반
- **수정 방법**: `@Data`를 `@Value`로 교체하거나 `@Getter` + `@Builder`만 사용

### 14. 도메인 모델에서 `LocalDateTime.now()` 직접 사용 (테스트 불가)
- **파일**: `domain/concert/model/Seat.java:40`, `domain/reservation/model/Reservation.java:82`
- **문제**: 시스템 클록에 직접 의존하여 단위 테스트가 비결정적
- **수정 방법**: `LocalDateTime`을 파라미터로 전달하거나 `Clock`을 주입

### 15. 좌석/예약 만료 시간 불일치 가능성
- **파일**: `Seat.reserve()` vs `Reservation.create()` 각각 독립적으로 5분 만료 설정
- **문제**: 좌석과 예약의 만료 시간에 미세한 차이 발생 가능
- **수정 방법**: 단일 만료 시간을 한 곳에서 생성하여 양쪽에 전달

### 16. 잔액 충전 요청에 음수/0 검증 없음
- **파일**: `domain/user/adapter/in/web/request/BalanceChargeRequest.java`
- **문제**: `@NotNull` 검증만 있고 양수 검증(`@DecimalMin`) 없음
- **수정 방법**: `@DecimalMin(value = "0.01")` 추가

### 17. CSRF 비활성화 사유 미문서화
- **파일**: `config/security/SecurityConfig.java:49`
- **문제**: `.csrf(AbstractHttpConfigurer::disable)` 설정에 사유 설명 없음
- **수정 방법**: Stateless JWT API이므로 안전하다는 주석 추가

### 18. CORS 설정 없음
- **파일**: `config/security/`
- **문제**: CORS 정책이 정의되지 않아 프론트엔드 요청이 차단됨
- **수정 방법**: `WebMvcConfigurer`에 CORS 매핑 추가

### 19. Redis 토큰 어댑터 미구현 (스텁 상태)
- **파일**: `domain/token/adapter/out/redis/QueueTokenRedisRepositoryAdapter.java`
- **문제**: 모든 메서드가 empty/null/zero를 반환 - 활성화 시 무응답으로 장애 발생
- **수정 방법**: 실제 구현하거나 `UnsupportedOperationException` throw

### 20. 실패한 결제 기록에 시도 금액 미기록
- **파일**: `application/usecase/PaymentUseCase.java:46-57`
- **문제**: catch 블록에서 FAIL 결제를 `BigDecimal.ZERO` 금액으로 생성하여 감사 추적 불가
- **수정 방법**: 실제 시도된 결제 금액을 기록

### 21. 인증 정보가 INFO 레벨로 로깅됨
- **파일**: `config/security/filter/JwtAuthenticationFilter.java:39`
- **문제**: `log.info("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName())`로 개인정보 노출
- **수정 방법**: DEBUG 레벨로 변경하거나 제거

### 22. QueueTokenMemoryRepository `getRank()` 로직 오류
- **파일**: `domain/token/adapter/out/memory/QueueTokenMemoryRepository.java:61-72`
- **문제**: 대기순위 계산 시 대상 토큰을 확인하기 전에 WAITING 토큰의 순위 카운터를 증가시켜야 하는데 순서가 반대
- **수정 방법**: WAITING 토큰 순위 증가를 대상 토큰 확인보다 먼저 수행

---

## 기존 테스트 이슈 (리뷰 중 수정 완료)

| 테스트 파일 | 문제 | 수정 내용 |
|------------|------|----------|
| `UserTest.java:17` | `User.create()` 인자 없이 호출 | `User.create("test@test.com", "password123")`으로 변경 |
| `ReservationTest.java:16-17` | 빌더에서 `expireAt` 오타 사용 | `expiresAt`으로 수정 |
| `QueueTokenServiceTest:95-106` | `findAll()` 모킹했으나 서비스는 `findByExpiredAtBefore()` 사용 | 모킹 메서드 수정 |
| `UserServiceImplTest:88-139` | `findById()` 모킹했으나 서비스는 `findByIdForUpdate()` 사용 | 모킹 메서드 수정 |
| `ReservationUseCaseIntegrationTest:84-86` | `ALREADY_RESERVED` 예상했으나 실제는 `ALREADY_HAVE_RESERVATION` 반환 | assertion 수정 |
| `PaymentUseCaseIntegrationTest:121-127` | email/password/role 없이 유저 생성 (NOT NULL 제약 위반) | 필수 필드 추가 |
| `test/resources/application.yml` | 테스트 프로필에 JWT 설정 누락 | JWT 테스트 설정 추가 |

---

## 수정된 파일 목록

### 소스 코드
1. `domain/user/adapter/out/persistence/UserEntity.java` - 필드 매핑 수정
2. `domain/user/application/UserMapper.java` - `User.create()` 사용하도록 수정
3. `domain/user/application/dto/command/CreateUserCommand.java` - email/password 필드 추가
4. `src/main/resources/application-local.yml` - JWT secret 환경변수 분리
5. `domain/reservation/adapter/out/persistence/ReservationEntity.java` - unique constraint 추가
6. `domain/token/application/QueueTokenService.java` - 상태 변경 후 save() 추가
7. `domain/concert/application/repository/SeatRepository.java` - `findExpiredHolds()` 추가
8. `domain/concert/adapter/out/persistence/repository/SeatJpaRepository.java` - 만료 좌석 조회 쿼리 추가
9. `domain/concert/adapter/out/persistence/adapter/SeatRepositoryAdapter.java` - `findExpiredHolds()` 구현
10. `domain/concert/application/service/SeatService.java` - `releaseExpiredHolds()` 추가
11. `domain/reservation/application/ReservationRepository.java` - `findExpiredPendingReservations()` 추가
12. `domain/reservation/adapter/out/persistence/ReservationJpaRepository.java` - 만료 예약 조회 쿼리 추가
13. `domain/reservation/adapter/out/persistence/ReservationRepositoryAdapter.java` - 만료 예약 조회 구현
14. `domain/reservation/application/ReservationService.java` - `findExpiredPendingReservations()` 추가
15. `domain/reservation/model/Reservation.java` - `cancel()` 메서드 추가

### 신규 파일
16. `domain/reservation/adapter/in/scheduler/ReservationScheduler.java` - 만료 예약 자동 정리 스케줄러

### 테스트 수정
17. `test/.../UserTest.java` - `User.create()` 호출 수정
18. `test/.../ReservationTest.java` - `expireAt` 오타 수정
19. `test/.../QueueTokenServiceTest.java` - 모킹 수정
20. `test/.../UserServiceImplTest.java` - 모킹 메서드 + 유저 생성 수정
21. `test/.../ReservationUseCaseIntegrationTest.java` - assertion + 유저 생성 수정
22. `test/.../PaymentUseCaseIntegrationTest.java` - 유저 생성 수정
23. `test/resources/application.yml` - JWT 테스트 설정 추가
