# 콘서트 예약 서비스 API 명세서

## 개요
콘서트 예약 서비스의 REST API 명세서입니다.

---

## 1. 토큰 관리 API

### 1.1 토큰 생성
- **URL**: `POST /api/v1/token`
- **Summary**: Token 생성
- **Description**: Token 생성 API
[ScheduleResponse.java](src/main/java/kr/hhplus/be/server/domain/concert/adapter/in/web/response/ScheduleResponse.java)
**Response:**
- **201 Created**: 토큰 정상 생성
```json
{
  "token": "string",
  "expiredAt": "2024-01-01 15:30:00",
  "rank": 42,
  "status": "WAITING"
}
```

### 1.2 토큰 정보 조회
- **URL**: `GET /api/v1/token`
- **Summary**: Token 정보 조회
- **Description**: Token 정보 조회 API

**Response:**
- **200 OK**: 토큰 정보 정상 조회
```json
{
  "token": "string",
  "expiredAt": "2024-01-01 15:30:00",
  "rank": 42,
  "status": "WAITING"
}
```

---

## 2. 콘서트 관리 API

### 2.1 콘서트 예약 가능 날짜 조회
- **URL**: `GET /api/v1/concerts/{concertId}/schedules`
- **Summary**: 콘서트 예약 가능 날짜 조회
- **Description**: 콘서트 예약 가능 날짜 조회 API

**Parameters:**
- `concertId` (Path) - 콘서트 ID

**Response:**
- **200 OK**: 콘서트 예약 가능 날짜 정상 조회
```json
[
  {
    "concertId": 123,
    "scheduleId": 1,
    "concertDate": "2024-01-15",
    "availableSeat": 42,
    "totalSeat": 50
  }
]
```

### 2.2 콘서트 예약 가능 좌석 조회
- **URL**: `GET /api/v1/concerts/schedules/{schedulesId}/seats`
- **Summary**: 콘서트 예약 가능 좌석 조회
- **Description**: 콘서트 예약 가능 좌석 조회 API

**Parameters:**
- `schedulesId` (Path) - 스케줄 ID

**Response:**
- **200 OK**: 콘서트 예약 가능 좌석 정상 조회
```json
{
  "concertId": 123,
  "scheduleId": 1,
  "seatId": 15,
  "status": "AVAILABLE"
}
```

---

## 3. 예약 관리 API

### 3.1 좌석 예약
- **URL**: `POST /api/v1/reservations`
- **Summary**: 좌석 예약
- **Description**: 좌석 예약 API

**Request Body:**
```json
{
  "concertId": 123,
  "scheduleId": 1,
  "seatId": 15
}
```

**Response:**
- **201 Created**: 정상 예약 (응답 본문 없음)

---

## 4. 유저 관리 API

### 4.1 잔액 충전
- **URL**: `POST /api/v1/users/balance/charge`
- **Summary**: 잔액 충전
- **Description**: 잔액 충전 API

**Request Body:**
```json
{
  "amount": 10000
}
```

**Response:**
- **200 OK**: 잔액 정상 충전 (응답 본문 없음)

### 4.2 잔액 조회
- **URL**: `GET /api/v1/users/balance`
- **Summary**: 잔액 조회
- **Description**: 잔액 조회 API

**Response:**
- **200 OK**: 잔액 정상 조회
```json
{
  "amount": 50000
}
```

---

## 5. 결제 관리 API

### 5.1 결제 처리
- **URL**: `POST /api/v1/payments`
- **Summary**: 결제
- **Description**: 결제 API

**Request Body:**
```json
{
  "reservationId": 123,
  "amount": 50000
}
```

**Response:**
- **200 OK**: 정상 결제 (응답 본문 없음)

---

## 데이터 모델

### TokenResponse
```json
{
  "token": "대기열 토큰 ID",
  "expiredAt": "토큰 만료 시간 (yyyy-MM-dd HH:mm:ss)",
  "rank": "대기열 순번 (1부터 시작)",
  "status": "토큰 상태 (WAITING, ACTIVE, EXPIRED)"
}
```

### ScheduleResponse
```json
{
  "concertId": "콘서트 ID",
  "scheduleId": "일정 ID",
  "concertDate": "콘서트 날짜 (yyyy-MM-dd)",
  "availableSeat": "남은 좌석 수",
  "totalSeat": "총 좌석 수"
}
```

### SeatResponse
```json
{
  "concertId": "콘서트 ID",
  "scheduleId": "일정 ID",
  "seatId": "좌석 ID",
  "status": "좌석 상태 (SeatStatus enum)"
}
```

### ReserveSeatRequest
```json
{
  "concertId": "콘서트 ID",
  "scheduleId": "스케줄 ID", 
  "seatId": "좌석 ID"
}
```

### BalanceChargeRequest
```json
{
  "amount": "충전 잔액"
}
```

### BalanceResponse
```json
{
  "amount": "현재 잔액"
}
```

### PayRequest
```json
{
  "reservationId": "예약 ID",
  "amount": "결제 금액"
}
```

---

## 주의사항

1. 모든 API는 대기열 토큰 검증이 필요합니다 (토큰 발급 API 제외)
2. 좌석 예약 시 5분간 임시 배정됩니다
3. 결제 완료 후 좌석 소유권이 확정됩니다
4. 동시성 처리를 고려한 구현이 필요합니다