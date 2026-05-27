# 코드 리뷰 보고서

**프로젝트**: 항해플러스 콘서트 예약 시스템
**작성일**: 2026-05-25 (5/22 1차 리뷰 통합 + ECC 전체 재리뷰)
**리뷰어**: Claude Code (Opus 4.7 + everything-claude-code:code-reviewer)
**범위**: 전체 프로젝트 (Java 153파일 / 약 6,025줄). `src/main/java/kr/hhplus/be/aop/` (학습용) 제외.

> 5/22 1차 리뷰 원문은 git 이력 (`01de406 체크 필요 코드 커밋`) 에 보존됨. 본 문서는 그 결과 + ECC 재리뷰를 통합한 단일 통합본입니다.

---

## 0. 이 보고서의 위치

5/22 1차 리뷰 결과 중 **CRITICAL 5건만 수정 완료** (`4bef1ab 임시 커밋`)되고, 나머지 **HIGH 7건 + MEDIUM 10건은 미수정** 상태입니다. 5/25 ECC 재리뷰에서 **신규 26건**을 추가 발견하여 현재 미해결 합계는 **43건**입니다.

> 본 보고서는 **STEP06 (분산락/캐싱) 진입 전 정비**를 목적으로 우선순위를 재책정합니다.
>
> 이슈 ID 표기:
> - `*-OLD-N` = 5/22 1차 리뷰에서 식별된 미해결 항목 (원래 번호 보존)
> - `*-NEW-N` = 5/25 ECC 재리뷰에서 신규 식별

### 0.1 미해결 요약

| 분류 | 1차 미해결 | 신규 발견 | **현재 합계** |
|---|---:|---:|---:|
| CRITICAL | 0 | 3 | **3** |
| HIGH | 7 | 9 | **16** |
| MEDIUM | 10 | 9 | **19** |
| LOW | 0 | 5 | **5** |
| **합계** | 17 | 26 | **43** |

### 0.2 브랜치 정리 노트

- `origin/temp/check-01`: merge base `393e744` 위에 `01de406 체크 필요 코드 커밋` 하나뿐. 코드 변경은 모두 main의 `4bef1ab`에 동일 내용으로 반영됨. AOP 학습 코드(252줄) 결여로 오히려 main보다 뒤떨어진 상태. **삭제 가능**.
- `feat/redis` 로컬 브랜치 + `stash@{0}`, `stash@{1}` 살아있음. STEP06 시작 시 복구 필요.

---

## 1. CRITICAL 이슈 (신규) — Redis 들어가기 전에 반드시

### C-NEW-1. 결제 실패(FAIL) 레코드가 사실상 절대 저장되지 않음 — 트랜잭션 롤백

- **파일**: `application/usecase/PaymentUseCase.java:29-55`
- **문제**: `pay()`가 `@Transactional`로 묶인 상태에서 결제 도중 `BusinessException` 발생 시 트랜잭션이 rollback-only로 마킹됨. catch 블록의 `paymentService.create(failPayment)`는 `PROPAGATION_REQUIRED`로 같은 트랜잭션에 참여 → 메서드 종료 시 전체 롤백 → **FAIL 이력이 DB에 남지 않는다**.
- **영향**: 결제 실패 감사/디버깅 불가. v1-MEDIUM ⑳(BigDecimal.ZERO 금액 문제) 이전에 FAIL 레코드 자체가 안 쓰여진다.
- **수정 방법**: `PaymentService.createFailRecord(...)`를 `@Transactional(propagation = REQUIRES_NEW)`로 분리하거나, 실패 기록을 ApplicationEventPublisher 비동기 이벤트(STEP08 EDA 도입 시 자연스럽게 매칭)로 분리.

### C-NEW-2. 큐 토큰 발급 API가 인증 사용자와 무관한 userId 수용

- **파일**: `domain/token/adapter/in/web/TokenController.java:36-43`
- **문제**: `IssueTokenRequest.userId`를 그대로 사용. `@AuthenticationPrincipal` 검증 없음. 임의 userId로 큐 슬롯 무한 점거 가능.
- **영향**: 큐 시스템 신뢰 붕괴, 슬롯 고갈/DoS, 타사용자 명의 자리 선점.
- **수정 방법**: `@AuthenticationPrincipal AuthUser authUser`만 사용, request body의 `userId` 필드 제거.

### C-NEW-3. Reservation UNIQUE 제약으로 정상 시나리오 차단

- **파일**: `domain/reservation/adapter/out/persistence/ReservationEntity.java:34-36`, `domain/reservation/application/ReservationService.java:40-44`
- **문제**: v1-CRITICAL ③에서 추가한 `(user_id, seat_id)` UNIQUE 제약 + `ReservationService.create()`가 **상태 무관 `findByUserIdAndSeatId().ifPresent → throw`**. 동일 유저가 한 번 예약 후 CANCELLED/취소되면 같은 좌석에 재예약 불가.
- **영향**: 동시 예약 방지는 됐지만, **예약 → 결제 포기 → 같은 좌석 다시 예약** 같은 정상 흐름이 막힘. UX 손상.
- **수정 방법**: UNIQUE 제거하고 애플리케이션 로직으로 `findByUserIdAndSeatIdAndStatusIn([PENDING_PAYMENT, CONFIRMED])` 체크. 또는 `WHERE status IN (...)` 부분 인덱스가 가능한 DB로 변경(MySQL은 미지원, PostgreSQL은 가능).

---

## 2. HIGH 이슈

### v1에서 이월 (모두 미수정 확인됨)

| ID | 항목 | 위치 |
|---|---|---|
| H-OLD-6 | Auth 컨트롤러 `@Valid`/`@Email`/비번 강도 검증 누락 | `domain/auth/adapter/in/web/AuthController.java`, `SignInRequest`, `SignUpRequest` |
| H-OLD-7 | 도메인 모델 직접 변이 (불변성 위반) | `Seat.java`, `User.java:27,34`, `Payment.java:42`, `Reservation.java:41,67`, `QueueToken.java` |
| H-OLD-8 | `AuthUser.from()` NPE 위험 | `config/security/AuthUser.java:41` |
| H-OLD-9 | 인증 엔드포인트 Rate Limiting 없음 | `AuthController` (Bucket4j 등 미도입) |
| H-OLD-10 | JWT 무효화/로그아웃 메커니즘 없음 | `domain/auth/application/AuthService.java` |
| H-OLD-11 | `QueueTokenMemoryRepository.save()`가 항상 `waitingQueue.offer()` → 중복 enqueue | `domain/token/adapter/out/memory/QueueTokenMemoryRepository.java:84-89` |
| H-OLD-12 | ReservationUseCase 좌석 상태 전환 비원자성 | `application/usecase/ReservationUseCase.java:35` |

### 신규

### H-NEW-1. `getReservationWithLock`이 `@Transactional(readOnly = true)`로 선언

- **파일**: `domain/reservation/application/ReservationService.java:27-31`
- **문제**: PESSIMISTIC_WRITE 락을 잡는 메서드가 readOnly. 외부 트랜잭션 참여 시엔 동작하지만 propagation 변경 시 락이 무효화될 수 있고, Hibernate readOnly 최적화와 write 락 의도가 모순.
- **수정 방법**: `@Transactional` (readOnly 제거).

### H-NEW-2. PaymentService가 어댑터 구현체에 직접 의존 (헥사고날 위반)

- **파일**: `domain/payment/application/PaymentService.java:15`
- **문제**: `private final PaymentRepositoryAdapter repository;` — 인터페이스 `PaymentRepository`가 있는데도 구현체 주입.
- **수정 방법**: 타입을 `PaymentRepository`로 변경.

### H-NEW-3. `[DEV]` 표시된 유저 생성/조회 API가 누구나 호출 가능

- **파일**: `domain/user/adapter/in/web/UserController.java:71-93`
- **문제**: 권한 체크 없음. 일반 유저도 `POST /api/v1/users`로 임의 생성, `GET /api/v1/users`로 전체 유저(email/balance) 조회 가능.
- **수정 방법**: `@PreAuthorize("hasRole('ADMIN')")` 또는 `@Profile("local","dev")`로 빈 등록 제한.

### H-NEW-4. AuthService에 `@Transactional` 누락

- **파일**: `domain/auth/application/AuthService.java:18-72`
- **문제**: 클래스/메서드 모두 트랜잭션 어노테이션 없음. signUp(쓰기)/signIn(readOnly)/refresh 모두 누락.
- **수정 방법**: 클래스에 `@Transactional(readOnly=true)`, signUp에 메서드 레벨 `@Transactional`.

### H-NEW-5. `SeatEntity.toModel()`이 LAZY 페치된 `schedule.getId()` 호출 — `LazyInitializationException` 위험 + N+1

- **파일**: `domain/concert/adapter/out/persistence/entity/SeatEntity.java:71-80`
- **문제**: `schedule`이 `FetchType.LAZY`인데 `toModel()`에서 `this.schedule.getId()`. `open-in-view: false`이므로 트랜잭션 종료 후 호출 시 예외. `findExpiredHolds`/`findBySchedule_Id`에서 좌석 N개당 schedule 추가 SELECT.
- **수정 방법**: ID만 필요하므로 외래키를 별도 String 컬럼으로 매핑(`@Column(name="schedule_id", insertable=false, updatable=false)`) 또는 fetch join.

### H-NEW-6. 같은 엔티티에 `@Version` (낙관 락) + `findByIdWithLock` (비관 락) 혼용

- **파일**: `domain/user/adapter/out/persistence/UserEntity.java:57-58`, `domain/user/application/UserService.java:42-58`
- **문제**: balance 충전/차감에서 비관 락으로 시리얼라이즈해도 `@Version` 충돌이 추가로 발생 가능. 의도 불명.
- **수정 방법**: 비관 락 채택 시 `@Version` 제거, 또는 `findByIdWithLock` 제거하고 낙관 락 + 재시도 전략.

### H-NEW-7. `TokenScheduler`가 100초마다 1개씩만 activate

- **파일**: `domain/token/adapter/in/scheduler/TokenScheduler.java:14-27`
- **문제**: `fixedDelay = 100000` (ms), 한 번에 1개. 시간당 36명. 더불어 메모리 어댑터의 save 중복 enqueue 버그(H-OLD-11)와 결합되어 activate 후 재save 시 같은 ID가 waitingQueue에 중복 적재.
- **수정 방법**: 처리 주기·배치 크기를 설정값으로 분리, save 중복 enqueue 수정과 함께 진행.

### H-NEW-8. `Seat.isOwnerBy` / `Reservation.isOwnedBy` NPE 가능

- **파일**: `domain/reservation/model/Reservation.java:78-80`, `domain/concert/model/Seat.java:58-60`
- **문제**: `this.userId.equals(userId)` 형태. `this.userId`가 null(미점유 상태)일 때 NPE.
- **수정 방법**: `Objects.equals(this.userId, userId)`.

### H-NEW-9. `PaymentEntity`에 생성/수정 시각 컬럼 없음

- **파일**: `domain/payment/adapter/out/persistence/PaymentEntity.java:22-75`
- **문제**: `@EntityListeners` 없음, `created_at`/`updated_at` 없음. 결제 시점 추적 불가 → 정산/감사 불가.
- **수정 방법**: `ReservationEntity`처럼 `AuditingEntityListener` + `@CreatedDate`/`@LastModifiedDate` 추가.

---

## 3. MEDIUM 이슈

### v1에서 이월 (모두 미수정 확인됨)

| ID | 항목 | 위치 |
|---|---|---|
| M-OLD-13 | Command DTO에 `@Data` 사용 (setter 생성) | `application/dto/ReserveSeatCommand.java:6`, `PayCommand.java:6` |
| M-OLD-14 | 도메인 모델에서 `LocalDateTime.now()` 직접 사용 | `Seat.java:40,63`, `Reservation.java:71,86` |
| M-OLD-15 | 좌석/예약 만료 시간이 별개 5분 | `Seat.reserve()` vs `Reservation.create()` |
| M-OLD-16 | `BalanceChargeRequest` 양수 검증 누락 | `domain/user/adapter/in/web/request/BalanceChargeRequest.java` |
| M-OLD-17 | CSRF 비활성 사유 주석 없음 | `config/security/SecurityConfig.java:49` |
| M-OLD-18 | CORS 설정 없음 | `config/security/` 전체 |
| M-OLD-19 | Redis 토큰 어댑터 스텁 상태 | `domain/token/adapter/out/redis/QueueTokenRedisRepositoryAdapter.java` (모든 메서드 empty/null/zero) |
| M-OLD-20 | 실패 결제 기록 시 `BigDecimal.ZERO` (C-NEW-1과 함께 처리) | `application/usecase/PaymentUseCase.java:46-57` |
| M-OLD-21 | JwtFilter가 인증 사용자명을 INFO 레벨 로깅 | `config/security/filter/JwtAuthenticationFilter.java:39` |
| M-OLD-22 | `QueueTokenMemoryRepository.getRank()` 카운터 순서 오류 | `QueueTokenMemoryRepository.java:61-72` |

### 신규

### M-NEW-1. ReservationScheduler가 cancel과 seat release를 별도 트랜잭션으로 분리

- **파일**: `domain/reservation/adapter/in/scheduler/ReservationScheduler.java:22-36`
- **문제**: reservation의 `expiresAt`(5분) cancel과 seat의 `holdExpiresAt`(5분) release가 서로 다른 쿼리/시점. 사이에 사용자가 결제 시도하면 중간 상태 노출. 각 reservation cancel은 개별 catch로 swallowing → 부분 실패가 누적.
- **수정 방법**: 단일 트랜잭션에서 reservation 기준으로 매핑된 seat까지 한꺼번에 release. M-OLD-15와 합쳐 만료 시점/처리를 일원화.

### M-NEW-2. `Concert.scheduleIds` 필드가 entity에서 채워지지 않음 (죽은 로직)

- **파일**: `domain/concert/adapter/out/persistence/entity/ConcertEntity.java:53-61`, `domain/concert/model/Concert.java:27-31`
- **문제**: 도메인 모델에 `scheduleIds`와 `hasSchedules()`는 있지만 `ConcertEntity.toModel()`이 채우지 않아 항상 빈 리스트 → `hasSchedules()`는 항상 false.
- **수정 방법**: 필드/메서드 삭제 또는 별도 쿼리로 로드.

### M-NEW-3. 콘서트 조회 엔드포인트 스텁 응답

- **파일**: `domain/concert/adapter/in/web/ConcertController.java:62-67` (`getConcertAvailableDate`), `ConcertScheduleController.java:40-45` (`getConcertAvailableSeat`)
- **문제**: 빈 리스트/더미 응답. Swagger엔 정상 응답처럼 노출.
- **수정 방법**: 구현 완성 또는 `@Hidden` 처리. (STEP04~05 과제 범위)

### M-NEW-4. `SeatResponse`/`ScheduleResponse` ID 타입이 Long (DB는 UUID String)

- **파일**: `domain/concert/adapter/in/web/response/SeatResponse.java:11-21`, `ScheduleResponse.java:13-17`
- **문제**: 타입 불일치 + Getter 누락 → JSON 직렬화 동작은 기본 필드 visibility에 의존.
- **수정 방법**: `String` 타입, `@Getter`, `fromModel()` 정적 팩토리 추가.

### M-NEW-5. `AuthController.refresh` 엔드포인트 미노출

- **파일**: `domain/auth/adapter/in/web/AuthController.java`
- **문제**: `AuthService.refresh()`는 구현됐지만 컨트롤러 매핑 없음. access 1시간 만료 후 재로그인 강제.
- **수정 방법**: `POST /api/v1/auth/refresh` 추가.

### M-NEW-6. `SignUpRequest` 이메일 포맷/비밀번호 정책 검증 누락

- **파일**: `domain/auth/adapter/in/web/request/SignUpRequest.java:8-17`
- **문제**: `@NotBlank`만 있고 `@Email`, `@Size(min=8)`, `@Pattern` 없음. → H-OLD-6의 구체 사례.
- **수정 방법**: H-OLD-6 묶어서 처리.

### M-NEW-7. `TokenValidationInterceptor`가 SecurityContext와 별도 경로로 동작

- **파일**: `config/interceptor/TokenValidationInterceptor.java:36-37`, `config/interceptor/WebMvcConfig.java:16-30`
- **문제**: 인터셉터가 `request.setAttribute("userId", ...)`로 별도 컨텍스트 세팅. 컨트롤러들은 `@AuthenticationPrincipal AuthUser`만 사용 → 인터셉터가 세팅한 userId는 **어디서도 안 쓰임**. 데드 코드 + 인증 경로 이원화.
- **수정 방법**: setAttribute 제거. 큐 토큰 검증은 유지하되 SecurityContext의 userId와 큐 토큰의 userId 일관성 검증 추가.

### M-NEW-8. `User.deductBalance`에 음수 검증 없음

- **파일**: `domain/user/model/User.java:30-35`
- **문제**: `addBalance`는 음수 차단, `deductBalance`는 미검증. 음수 amount 시 잔액이 오히려 증가.
- **수정 방법**: `if (amount.compareTo(BigDecimal.ZERO) <= 0) throw ...`.

### M-NEW-9. `JwtTokenPayload.from(Claims)`에서 null 클레임 NPE

- **파일**: `config/security/jwt/JwtTokenPayload.java:40-47`
- **문제**: `claims.get(CLAIM_ID).toString()` — 변조된 토큰에서 claim 누락 시 NPE. `validateToken`은 통과하지만 파싱에서 죽음 → 500.
- **수정 방법**: null 체크 후 `BusinessException(CommonErrorCode.TOKEN_ERROR)`.

---

## 4. LOW 이슈 (신규)

### L-NEW-1. `Reservation.java`에 미사용 `BigInteger` import
- **파일**: `domain/reservation/model/Reservation.java:4`

### L-NEW-2. `BaseResponse`의 `@Setter`가 final 필드와 충돌
- **파일**: `shared/dto/BaseResponse.java:11-25` — 클래스 레벨 `@Setter`인데 일부 필드는 final. 응답 DTO 불변성 깨짐.
- **수정**: `@Setter` 제거하고 builder/factory만 사용.

### L-NEW-3. CommandFactory vs CommandMapper 명명 혼재
- **파일**: `domain/reservation/adapter/in/web/factory/ReservationCommandFactory.java`, `domain/payment/adapter/in/web/factory/PaymentCommandFactory.java`
- **문제**: Auth/User/Concert는 `*CommandMapper` static, Reservation/Payment는 `*Factory` `@Component`. 두 패턴 혼재.
- **수정**: `*CommandMapper`로 통일.

### L-NEW-4. `ConcertController.createConcert`가 도메인 모델 그대로 응답
- **파일**: `domain/concert/adapter/in/web/ConcertController.java:71-76`
- **문제**: 다른 메서드는 `ConcertResponse.fromModel`을 쓰는데 여기만 raw `Concert` 반환.

### L-NEW-5. `TokenValidationInterceptor`의 헤더 누락/형식 오류가 같은 에러 코드
- **파일**: `config/interceptor/TokenValidationInterceptor.java:28-30`
- **문제**: `tokenId == null` 검사와 `isBlank` 중복, 누락과 형식 오류 구분 안 됨.

---

## 5. 잘 작성된 부분 (참고)

- **`GlobalExceptionHandler`**: BusinessException/ServerErrorException/MethodArgumentNotValid/Exception을 분리해 BaseResponse로 일관 처리.
- **`JwtTokenProvider.validateToken`**: jjwt의 SignatureException/ExpiredJwtException 등을 세분화해 의미별로 로깅.
- **헥사고날 디렉토리 구조**: 6개 도메인 중 5개는 port(`application/repository`) + adapter(`adapter/out/persistence/adapter`) 분리가 깔끔. PaymentService 1건(H-NEW-2)만 예외.
- **대부분의 Command DTO**: `CreateReservationCommand`, `CreateConcertCommand`, `CreateUserCommand` 등은 builder 기반 immutable. (`ReserveSeatCommand`, `PayCommand`만 `@Data` 잔존 — M-OLD-13)

---

## 6. STEP06 (Redis 분산락/캐싱) 진입 전 정비 권장 순서

> "분산락을 어디에 어떻게 적용할지" 결정하기 전에 **현재 코드의 동시성/정합성 가정이 무너진 곳을 먼저 닫는다**는 원칙.

### Phase 1 — 정합성 차단해제 (반드시, 0.5~1일)

| 순서 | 항목 | 이유 |
|---|---|---|
| 1 | **C-NEW-3** Reservation UNIQUE 해제 + status 기반 중복 체크 | 정상 시나리오 차단 중. 분산락 적용해도 이 문제 안 풀림 |
| 2 | **C-NEW-2** TokenController userId를 `@AuthenticationPrincipal`로 변경 | 보안 구멍 + 큐 시스템 무력화 |
| 3 | **H-NEW-5** SeatEntity LAZY → schedule_id 컬럼 직접 매핑 | open-in-view:false 환경에서 운영 중 폭발 가능 |

### Phase 2 — 트랜잭션 경계 정리 (분산락 직전, 1일)

| 순서 | 항목 | 이유 |
|---|---|---|
| 4 | **H-NEW-1** `getReservationWithLock` readOnly 제거 | 분산락 + 비관락 혼용 전 락 의미 명확화 |
| 5 | **H-OLD-12 + H-NEW-4** ReservationUseCase 비원자성 + AuthService `@Transactional` | 분산락 적용 지점 (예약 흐름) 정리 |
| 6 | **C-NEW-1** PaymentUseCase FAIL 기록 트랜잭션 분리 (REQUIRES_NEW) | 결제 분산락 적용 직전 |
| 7 | **H-NEW-6** User 락 전략 일관화 (`@Version` vs 비관락 중 하나) | 잔액 차감에 분산락 추가 시 락 충돌 회피 |

### Phase 3 — 큐 도메인 정비 (Redis 어댑터 실구현 직전, 0.5일)

| 순서 | 항목 | 이유 |
|---|---|---|
| 8 | **H-OLD-11 + M-OLD-22 + H-NEW-7** 메모리 어댑터 save 중복 enqueue, getRank, TokenScheduler 처리량 | Redis로 갈아끼우기 전 비교 기준 확립. Redis 구현이 의미적으로 동등한지 검증 가능 |
| 9 | **M-OLD-19** Redis 어댑터 빈 스텁 → 실구현 시작 | feat/redis stash 복구 후 |

### Phase 4 — 보안/검증 일괄 (병행 가능, 0.5일)

| 순서 | 항목 | 이유 |
|---|---|---|
| 10 | **H-OLD-6 + M-NEW-6** AuthController `@Valid`, SignUp/SignIn 검증 | 작업량 작음, 임팩트 큼 |
| 11 | **H-OLD-8 + M-NEW-9** AuthUser.from / JwtTokenPayload.from NPE 가드 | |
| 12 | **H-NEW-3** DEV API 권한 가드 | |
| 13 | **M-OLD-21** Jwt 필터 INFO 로그 → DEBUG | |
| 14 | **H-OLD-10** JWT 로그아웃/블랙리스트 → **Redis 도입과 함께 진행** (블랙리스트 키 저장소가 Redis) |

### Phase 5 — 분산락 미적용 (STEP06 본 작업) — Phase 1~4 끝난 뒤

- **AOP 학습 자산 활용**: `kr.hhplus.be.aop.AspectV5Order` 패턴을 응용한 `@DistributedLock(key, scope)` 커스텀 어노테이션
- **적용 지점**:
  - 예약 생성: `seatId` 키, `seatService.reserve()` 범위
  - 결제: `userId` 키, `PaymentUseCase.pay()` 범위 (DB Tx 안쪽 vs 바깥쪽 결정 필요)
  - 잔액 충전: `userId` 키
- **DB Tx와 분산락 혼용 시 주의**: 락 해제는 트랜잭션 커밋 후, 즉 락이 트랜잭션을 감싸는 형태가 안전 (STEP06 발제 평가 기준 그대로)

### Phase 6 — STEP06 이후 별도 PR로 (낮은 우선순위)

- **H-OLD-7** 도메인 모델 불변성 리팩토링 (5개 도메인 동시 손대야 함 → 별도 작업)
- **M-OLD-13 + M-OLD-14** Command `@Data` → `@Value`, `Clock` 주입
- **M-NEW-2** 죽은 `Concert.scheduleIds` 정리
- **L-NEW-1~5** LOW 일괄 정리

---

## 7. 작업량 추정

| Phase | 항목 수 | 추정 |
|---|---:|---|
| 1. 정합성 차단해제 | 3 | 0.5~1일 |
| 2. 트랜잭션 정리 | 4 | 1일 |
| 3. 큐 도메인 | 2 (이슈 4건) | 0.5일 |
| 4. 보안 일괄 | 5 | 0.5일 |
| **STEP06 진입 준비 소계** | **14** | **2.5~3일** |
| 5. 분산락 본 작업 | — | 5~7일 (STEP06 권장기간 14일의 절반) |
| 6. 후순위 정리 | 26 | 별도 |

---

## 8. 처리 추적용 체크리스트

### Phase 1 — 정합성 차단해제
- [ ] C-NEW-3 Reservation UNIQUE 해제 + status 체크
- [ ] C-NEW-2 TokenController `@AuthenticationPrincipal`
- [ ] H-NEW-5 SeatEntity schedule_id 직접 매핑

### Phase 2 — 트랜잭션 경계
- [ ] H-NEW-1 `getReservationWithLock` readOnly 제거
- [ ] H-OLD-12 ReservationUseCase 비원자성
- [ ] H-NEW-4 AuthService `@Transactional`
- [ ] C-NEW-1 PaymentUseCase FAIL 기록 REQUIRES_NEW
- [ ] H-NEW-6 User 락 전략 일관화

### Phase 3 — 큐 도메인
- [ ] H-OLD-11 메모리 어댑터 save 중복 enqueue
- [ ] M-OLD-22 getRank 카운터 순서
- [ ] H-NEW-7 TokenScheduler 처리량
- [ ] M-OLD-19 Redis 어댑터 실구현 (feat/redis stash 복구 후)

### Phase 4 — 보안/검증
- [ ] H-OLD-6 + M-NEW-6 Auth `@Valid` + Request 검증
- [ ] H-OLD-8 AuthUser.from NPE 가드
- [ ] M-NEW-9 JwtTokenPayload.from NPE 가드
- [ ] H-NEW-3 DEV API 권한 가드
- [ ] M-OLD-21 Jwt 필터 로그 레벨
- [ ] H-OLD-10 JWT 블랙리스트 (Redis 도입과 함께)
- [ ] H-OLD-9 Rate Limiting (Bucket4j)

### Phase 5 — STEP06 본 작업
- [ ] `feat/redis` 브랜치 + stash 복구
- [ ] `@DistributedLock` AOP 구현
- [ ] 예약/결제/잔액 충전에 분산락 적용
- [ ] Redis Testcontainers 통합 테스트
- [ ] (선택) 콘서트 일정/좌석 조회 캐싱

### Phase 6 — 후순위
- [ ] H-OLD-7 도메인 모델 불변성 (별도 PR)
- [ ] M-OLD-13 Command @Data → @Value/@Builder
- [ ] M-OLD-14 Clock 주입
- [ ] M-OLD-15 만료 시간 단일화
- [ ] M-OLD-16 BalanceChargeRequest `@DecimalMin`
- [ ] M-OLD-17 CSRF 비활성 주석
- [ ] M-OLD-18 CORS 설정
- [ ] M-OLD-20 FAIL 결제 금액 (C-NEW-1과 함께)
- [ ] M-NEW-1 ReservationScheduler 트랜잭션 일원화
- [ ] M-NEW-2 죽은 `Concert.scheduleIds` 정리
- [ ] M-NEW-3 콘서트 조회 스텁 메서드 구현/숨김
- [ ] M-NEW-4 SeatResponse/ScheduleResponse 타입 정정
- [ ] M-NEW-5 AuthController `/refresh` 엔드포인트
- [ ] M-NEW-7 TokenValidationInterceptor 정리
- [ ] M-NEW-8 User.deductBalance 음수 검증
- [ ] L-NEW-1 미사용 import 제거
- [ ] L-NEW-2 BaseResponse @Setter 제거
- [ ] L-NEW-3 CommandFactory → CommandMapper 통일
- [ ] L-NEW-4 createConcert 응답 DTO 변환
- [ ] L-NEW-5 토큰 헤더 에러 코드 세분화

### 브랜치 정리
- [ ] `origin/temp/check-01` 삭제
- [ ] `feat/redis` 로컬 브랜치 정리 (stash 복구 후)
